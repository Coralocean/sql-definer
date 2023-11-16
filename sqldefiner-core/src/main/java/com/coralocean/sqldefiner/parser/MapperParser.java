package com.coralocean.sqldefiner.parser;

import com.coralocean.sqldefiner.mock.Mock;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;


public class MapperParser {

    private final String baseMapperPath;
    private final PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public MapperParser(String baseMapperPath) {
        this.baseMapperPath = baseMapperPath;
    }
    public String getMapperXmlPath(Class<?> mapperClass) throws Exception {
        Resource resource = resourcePatternResolver.getResource("classpath:" + baseMapperPath + File.separator + mapperClass.getSimpleName() + ".xml");
        return resource.getFile().getAbsolutePath();
    }

    public List<Selector> parserMapper(Class<?> mapperClass) throws Exception{
        Mapper annotation = mapperClass.getAnnotation(Mapper.class);
        if ( annotation == null ) return new ArrayList<>();
        Method[] methods = mapperClass.getMethods();
        List<Selector> selectors = new ArrayList<>();
        String className = mapperClass.getName();
        for ( Method method : methods ) {
            Class<?> returnType = method.getReturnType();
            if ( returnType == void.class || returnType == Void.class ) {
                continue;
            }
            Map<String, MockColumn> parameterMap = new HashMap<>();
            Parameter[] parameters = method.getParameters();
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            for ( int i = 0; i < parameters.length; i++ ) {
                Parameter parameter = parameters[i];
                String parameterName;
                Param paramAnnotation = parameter.getAnnotation(Param.class);
                if ( null != paramAnnotation ) {
                    parameterName = paramAnnotation.value();
                } else {
                    parameterName = parameter.getName();
                }
                Type genericParameterType = genericParameterTypes[i];
                if ( isBasicType( genericParameterType ) ) {
                    MockColumn mockColumn = new MockColumn(parameterName, genericParameterTypes[i]);
                    mockColumn.setMock(parseMockAnnotation(paramAnnotation));
                    parameterMap.put(parameterName, mockColumn);
                } else {
                    Map<String, MockColumn> objectMap = parseObjParam(genericParameterType);
                    parameterMap.putAll(objectMap);
                }

            }
            Selector selector = new Selector();
            selector.setFullSelectId(className + "." + method.getName());
            selector.setSelectId(method.getName());
            selector.setParameterMap(parameterMap);
            selectors.add(selector);

        }
        return selectors;
    }


    private static Mock parseMockAnnotation(Object obj) {
        if ( obj instanceof Parameter ) {
            Parameter parameter = (Parameter) obj;
            return parameter.getAnnotation(Mock.class);
        } else if ( obj instanceof Field) {
            Field field = (Field) obj;
            return field.getAnnotation(Mock.class);
        } else {
           return obj.getClass().getAnnotation(Mock.class);
        }
    }

    private static Map<String, MockColumn> parseObjParam(Type param) throws Exception {
        String typeName = param.getTypeName();
        Class<?> aClass = Class.forName(typeName);
        Field[] fields = aClass.getDeclaredFields();
        Map<String, MockColumn> result = new HashMap<>();
        for ( Field field : fields ) {
            MockColumn mockColumn = new MockColumn(field.getName(), field.getGenericType());
            mockColumn.setMock( parseMockAnnotation( field ) );
            result.put(field.getName(), mockColumn);
        }
        return result;
    }

    private boolean isBasicType( Type type ) {
        return type.getClass().isPrimitive() || Number.class.isAssignableFrom( type.getClass() ) || type == String.class || type == Date.class || type.getClass().isArray();
    }

}
