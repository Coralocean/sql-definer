package com.coralocean.sqldefiner;

import com.coralocean.sqldefiner.dispatcher.ExplainDispatcher;
import com.coralocean.sqldefiner.dispatcher.MybatisDispatcher;
import com.coralocean.sqldefiner.parser.MapperParser;
import com.coralocean.sqldefiner.parser.MybatisXMLParser;
import com.coralocean.sqldefiner.parser.Selector;
import com.coralocean.sqldefiner.util.XmlUtil;
import org.dom4j.Document;

import java.util.*;

public class SqlDefender {

    private final MybatisXMLParser xmlParser;
    private final MapperParser mapperParser;

    private final MybatisDispatcher mybatisDispatcher;

    private final ExplainDispatcher explainDispatcher;

    public SqlDefender( MapperParser mapperParser, MybatisXMLParser xmlParser,
                        MybatisDispatcher mybatisDispatcher,
                        ExplainDispatcher explainDispatcher) {
        this.mapperParser = mapperParser;
        this.xmlParser = xmlParser;
        this.mybatisDispatcher = mybatisDispatcher;
        this.explainDispatcher = explainDispatcher;
    }

    public void audit(Class<?> mapperClass) {
        try {
            String mapperXmlPath = mapperParser.getMapperXmlPath(mapperClass);
            Document document = XmlUtil.parseXmlToDom(mapperXmlPath);
            List<Selector> selectors = mapperParser.parserMapper(mapperClass);
            List<Map<String, Object>> model = new ArrayList<>();
            Set<String> sqlSet = new HashSet<>();
            for (Selector selector : selectors) {
                List<Map<String, Object>> parseResultSet = xmlParser.parse(document, selector.getSelectId(), selector.getParameterMap());
                for ( Map<String, Object> parseResult : parseResultSet ) {
                    String sql = mybatisDispatcher.generateSQL(selector.getFullSelectId(), parseResult);
                    if ( !sqlSet.contains( sql )) {
                        Map<String, Object> explain = explainDispatcher.explain(sql);
                        model.add(explain);
                        sqlSet.add(sql);
                    }
                }
            }
            explainDispatcher.completeExplain(model);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
