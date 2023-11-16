package com.coralocean.sqldefiner.mock;

import com.coralocean.sqldefiner.parser.MockColumn;
import com.coralocean.sqldefiner.util.Incrementer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class MockDataGenerator {

    private static final Random RANDOM = new Random();
    private final JdbcTemplate jdbcTemplate;

    private final Map<String, List<Object>> PROPERTY_CACHE = new HashMap<>();

    private final Map<String, Set<String>> INDEX_CACHE = new HashMap<>();

    private final Incrementer incrementer = new Incrementer();


    public MockDataGenerator(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Object generateMockData(MockColumn mockColumn) {
        Mock mock = mockColumn.getMock();
        if (mock == null || mock.level() == 0) {
            return generateRandomMockData(mockColumn);
        } else {
            return generatePrecisMockData(mockColumn);
        }

    }


    public Object generateRandomMockData(MockColumn mockColumn) {
        Type type = mockColumn.getType();
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (clazz.equals(int.class) || clazz.equals(Integer.class) || clazz.equals(short.class) || clazz.equals(Short.class)) {
                return RANDOM.nextInt(100);
            } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                return RANDOM.nextLong();
            } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
                return RANDOM.nextDouble();
            } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
                return RANDOM.nextFloat();
            } else if (clazz.equals(String.class)) {
                return Long.toString(RANDOM.nextLong(), 36);
            } else if (clazz.isArray()) {
                return generateMockArray(mockColumn);
            } else if (Collection.class.isAssignableFrom(clazz)) {
                return generateMockCollection(ArrayList.class, mockColumn);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (Collection.class.isAssignableFrom(rawType)) {
                MockColumn newMockColumn = new MockColumn(mockColumn.getPropertyName(), actualTypeArguments[0]);
                newMockColumn.setMock(mockColumn.getMock());
                return generateMockCollection(rawType, newMockColumn);
            }
        }
        return null;
    }


    private Object[] generateMockArray(MockColumn mockColumn) {
        Type type = mockColumn.getType();
        Mock mock = mockColumn.getMock();
        int length = Optional.ofNullable(mock).map(Mock::length).orElse(10);
        Object[] array = (Object[]) Array.newInstance(type.getClass(), length);
        for (int i = 0; i < array.length; i++) {
            array[i] = generateMockData(mockColumn);
        }
        return array;
    }

    @SuppressWarnings("all")
    private Object generateMockCollection(Class<?> collectionType, MockColumn mockColumn) {
        Collection<Object> collection;
        try {
            collection = (Collection<Object>) collectionType.newInstance();
        } catch (Exception e) {
            collection = new ArrayList<>();
        }
        Mock mock = mockColumn.getMock();
        int length = Optional.ofNullable(mock).map(Mock::length).orElse(10);
        for (int i = 0; i < length; i++) {
            collection.add(generateMockData(mockColumn));
        }
        return collection;
    }


    private List<Object> preciseAndLazyLoad(String property, int limit, BiFunction<String, String, List<Object>> function) {
        if (!PROPERTY_CACHE.containsKey(property)) {
            String[] columnInfo = parseProperty(property);
            String tableName = columnInfo[0];
            String columnName = columnInfo[1];
            int newLimit = limit + 1;
            String sql = "select " + columnName + " from " + tableName + " limit " + newLimit;
            PROPERTY_CACHE.put(property, function.apply(sql, columnName));
        }
        return PROPERTY_CACHE.get(property);
    }

    public List<Object> precisAndLazyLoadColumnV1(String property, int limit) {
        return preciseAndLazyLoad(property, limit, (sql, columnName) -> jdbcTemplate.queryForList(sql)
                .stream().map(item -> item.get(columnName)).collect(Collectors.toList()));
    }

    public Set<String> precisAndLazyLoadIndexV1(String tableName) {
        if (!INDEX_CACHE.containsKey(tableName)) {
            String sql = "SHOW INDEX FROM " + tableName;
            List<String> query = jdbcTemplate.query(sql, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("COLUMN_NAME");
                }
            });
            INDEX_CACHE.put(tableName, new HashSet<>(query));
        }
        return INDEX_CACHE.get(tableName);
    }

    private static String[] parseProperty(String property) {
        return property.split("\\.");
    }

    public Object generatePrecisMockData(MockColumn mockColumn) {
        Mock mock = mockColumn.getMock();
        String property = mock.property();
        if (mock.level() == 0) {
            return generateRandomMockData(mockColumn);
        }
        List<Object> list = precisAndLazyLoadColumnV1(property, mock.length());
        Type type = mockColumn.getType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (Collection.class.isAssignableFrom(rawType)) {
                MockColumn newMockColumn = new MockColumn(mockColumn.getPropertyName(), actualTypeArguments[0]);
                newMockColumn.setMock(mockColumn.getMock());
                return generateMockCollection(rawType, newMockColumn);
            } else {
                return generatePrecisMockData(mockColumn);
            }
        } else {
            int increment = incrementer.increment(property);
            if (increment >= list.size()) {
                return generateRandomMockData(mockColumn);
            }
            return list.get(increment);
        }

    }

    public void initMockData() {
        incrementer.resetAll();
    }

}
