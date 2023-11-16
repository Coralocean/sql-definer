package com.coralocean.sqldefiner.dispatcher;

public class SelectTypeAnalysisResult {
    private String selectType;
    private String level = ExplainLevel.INFO.getLevel();
    private String selectTypeDescription;

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSelectTypeDescription() {
        return selectTypeDescription;
    }

    public void setSelectTypeDescription(String selectTypeDescription) {
        this.selectTypeDescription = selectTypeDescription;
    }
}
