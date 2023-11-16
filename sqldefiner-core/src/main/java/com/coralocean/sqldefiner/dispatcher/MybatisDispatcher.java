package com.coralocean.sqldefiner.dispatcher;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;


public class MybatisDispatcher {

    private final SqlSessionFactory sqlSessionFactory;
    private final String xmlScanPackage;

    public MybatisDispatcher(SqlSessionFactory sqlSessionFactory,
                             String xmlScanPackage) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.xmlScanPackage = xmlScanPackage;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    public String generateSQL(String selectId, Map<String, Object> parameters) {
        MappedStatement mappedStatement = sqlSessionFactory.getConfiguration().getMappedStatement(selectId);
        BoundSql boundSql = mappedStatement.getBoundSql(parameters);
        return handleParameterMap(parameters, mappedStatement, boundSql);
    }

    private String handleParameterMap(Map<String, Object> parameters, MappedStatement mappedStatement, BoundSql boundSql) {
        String sql = boundSql.getSql().replaceAll("\n", "").replaceAll("\\s+", " ");
        System.out.println(sql);
        Configuration configuration = mappedStatement.getConfiguration();
        MetaObject metaObject = configuration.newMetaObject(parameters);
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            String propertyName = parameterMapping.getProperty();
            if (metaObject.hasGetter(propertyName)) {
                Object value = metaObject.getValue(propertyName);
                sql = sql.replaceFirst("\\?", generateQueryColumnParam(value));
            } else {
                Object additionalParameter = boundSql.getAdditionalParameter(propertyName);
                String value = generateQueryColumnParam(additionalParameter);
                sql = sql.replaceFirst("\\?", value);
            }
        }
        return sql;
    }

    private String generateQueryColumnParam(Object value) {
        if (null == value) {
            return null;
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Date) {
            return "'" + DATE_FORMAT.format(value) + "'";
        } else if (value instanceof Collection) {
            StringBuilder builder = new StringBuilder();
            builder.append("(");
            for (Object next : (Collection<?>) value) {
                builder.append(generateQueryColumnParam(next)).append(",");
            }
            builder.delete(builder.length() - 2, builder.length());
            builder.append(")");
            return builder.toString();
        } else {
            return "'" + value + "'";
        }
    }



    public String getXmlScanPackage() {
        return xmlScanPackage;
    }

}
