package com.coralocean.sqldefiner.parser;


import java.util.Map;
public class Selector {
    private String fullSelectId;
    private String selectId;
    private Map<String, MockColumn> parameterMap;

    public String getFullSelectId() {
        return fullSelectId;
    }

    public void setFullSelectId(String fullSelectId) {
        this.fullSelectId = fullSelectId;
    }

    public String getSelectId() {
        return selectId;
    }

    public void setSelectId(String selectId) {
        this.selectId = selectId;
    }

    public Map<String, MockColumn> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, MockColumn> parameterMap) {
        this.parameterMap = parameterMap;
    }
}
