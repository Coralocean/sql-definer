package com.coralocean.sqldefiner.parser;


import com.coralocean.sqldefiner.mock.MockDataGenerator;
import com.coralocean.sqldefiner.parser.tokenHandler.IfTokenHandler;
import com.coralocean.sqldefiner.parser.tokenHandler.WhereTokenHandler;
import com.coralocean.sqldefiner.util.XmlUtil;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultComment;


import java.util.*;
import java.util.stream.Collectors;

public class MybatisXMLParser {

    private final MockDataGenerator mockDataGenerator;

    public MybatisXMLParser(MockDataGenerator mockDataGenerator) {
        this.mockDataGenerator = mockDataGenerator;
    }


//    public List<Map<String, Object>> parse(String xmlPath, String selectId, Map<String, MockColumn> parameterMap) throws Exception {
//        List<String> list = generateAllTestColumns(xmlPath, selectId);
//        list.removeIf(item -> !parameterMap.containsKey(item));
//        if (list.isEmpty()) {
//            return generateCombinationMaps(list, parameterMap);
//        }
//        return new ArrayList<>();
//    }

    public List<Map<String, Object>> parse(Document document, String selectId, Map<String, MockColumn> parameterMap) throws Exception {
        List<String> list = generateAllTestColumns(document, selectId);
        list.removeIf(item -> !parameterMap.containsKey(item));
        if (!list.isEmpty()) {
            return generateCombinationMaps(list, parameterMap);
        }
        return new ArrayList<>();
    }


//    private <T> T handleParam(List<String> combination, Class<T> tClass) throws Exception {
//        T instance = tClass.newInstance();
//        for (String condition : combination) {
//            Field declaredField = tClass.getDeclaredField(condition);
//            declaredField.setAccessible(true);
//            Class<?> type = declaredField.getType();
//            String setMethod = "set" + capitalizeFirstLetter(condition);
//
//            MockColumn mockColumn = new MockColumn(condition, declaredField.getGenericType());
//            mockColumn.setMock(declaredField.getAnnotation(Mock.class));
//            Object arg = mockDataGenerator.generateMockData(mockColumn);
//
//            Method method = tClass.getMethod(setMethod, type);
//            method.setAccessible(true);
//            method.invoke(instance, arg);
//        }
//        return instance;
//    }
//
//    private <T> Map<String, Object> handleParamMap(List<String> combination, Class<T> tClass) throws Exception {
//        T t = handleParam(combination, tClass);
//        return JSONObject.parseObject(JSONObject.toJSONString(t), new TypeReference<Map<String, Object>>() {
//        });
//    }

    private <T> Map<String, Object> handleParamMapV2(List<String> combination, Map<String, MockColumn> parameterMap) {
        Map<String, Object> result = new HashMap<>();
        mockDataGenerator.initMockData();
        for (String condition : combination) {
            MockColumn mockColumn = parameterMap.get(condition);
            Object arg = mockDataGenerator.generateMockData(mockColumn);
            result.put(condition, arg);
        }
        return result;
    }

    private static List<String> generateAllTestColumns(String xmlPath, String selectId) throws Exception {
        // 这里根据不同的标签内容选择不同的tokenParser
        Document document = XmlUtil.parseXmlToDom(xmlPath);
        return getTokens(document, selectId);
    }

    private static List<String> generateAllTestColumns(Document document, String selectId) throws Exception {
        // 这里根据不同的标签内容选择不同的tokenParser
        return getTokens(document, selectId);
    }

    private static List<String> getTokens(Document document, String selectId) {
        String sql = getOriSqlTemplate(document, selectId, true);
        final IfTokenHandler ifTokenHandler = new IfTokenHandler();
        final WhereTokenHandler whereTokenHandler = new WhereTokenHandler();
        new GenericTokenParser("<if test=\"", "\">", ifTokenHandler).parse(sql);
        new GenericTokenParser("<foreach collection=\"", "\">", whereTokenHandler).parse(sql);
        Set<String> resultSet = new HashSet<>();
        resultSet.addAll(ifTokenHandler.getTokenSet());
        resultSet.addAll(whereTokenHandler.getTokenSet());
        return new ArrayList<>(resultSet);
    }

    private static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private <T> List<Map<String, Object>> generateCombinationMaps(List<String> conditions, Map<String, MockColumn> parameterMap) throws Exception {
        List<List<String>> combinations = generateAllCombinations(conditions);
        List<Map<String, Object>> resultSet = new ArrayList<>();
        for (List<String> combination : combinations) {
            Map<String, Object> map = handleParamMapV2(combination, parameterMap);
            resultSet.add(map);
        }
        return resultSet;
    }

    private <T> List<Map<String, Object>> generateCombinationMaps(List<String> conditions, Map<String, MockColumn> parameterMap, String table) throws Exception {
        Set<String> indexes = mockDataGenerator.precisAndLazyLoadIndexV1(table);
        List<List<String>> combinations = generateAllCombinations(conditions, indexes);
        List<Map<String, Object>> resultSet = new ArrayList<>();
        for (List<String> combination : combinations) {
            Map<String, Object> map = handleParamMapV2(combination, parameterMap);
            resultSet.add(map);
        }
        return resultSet;
    }

    private static List<List<String>> generateAllCombinations(List<String> elements) {
        if (elements.isEmpty()) {
            List<List<String>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        String firstElement = elements.get(0);
        List<String> restElements = elements.subList(1, elements.size());

        List<List<String>> combinationsWithoutFirst = generateAllCombinations(restElements);
        List<List<String>> combinationWithFirst = new ArrayList<>();

        for (List<String> combination : combinationsWithoutFirst) {
            List<String> newCombination = new ArrayList<>(combination);
            newCombination.add(firstElement);
            combinationWithFirst.add(newCombination);
        }

        combinationsWithoutFirst.addAll(combinationWithFirst);
        return combinationsWithoutFirst;
    }

    private static List<List<String>> generateAllCombinations(List<String> elements, Set<String> indexes) {
        if (elements.isEmpty()) {
            List<List<String>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        String firstElement = elements.get(0);
        List<String> restElements = elements.subList(1, elements.size());
        List<List<String>> combinationsWithoutFirst = generateAllCombinations(restElements)
                .stream().filter( item -> item.stream().anyMatch( indexes::contains ))
                .collect(Collectors.toList());
        List<List<String>> combinationWithFirst = new ArrayList<>();

        for (List<String> combination : combinationsWithoutFirst) {
            List<String> newCombination = new ArrayList<>(combination);
            newCombination.add(firstElement);
            combinationWithFirst.add(newCombination);
        }

        combinationsWithoutFirst.addAll(combinationWithFirst);
        return combinationsWithoutFirst;
    }


    private static String getOriSqlTemplate(Document document, String selectId, Boolean saveOriFormat) {
        try {
            Element select = (Element) document.selectSingleNode("//select[@id='" + selectId + "']");
            StringBuilder tempStr = new StringBuilder();
            if (saveOriFormat) {
                List<?> list = select.content();
                for (Object object : list) {
                    if (!(object instanceof DefaultComment)) {
                        tempStr.append(((Node) object).asXML());
                    }
                }
            } else {
                tempStr = new StringBuilder(select.getTextTrim());
            }
            return tempStr.toString().replaceAll("\n", "").replaceAll("\\s+", " ");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
