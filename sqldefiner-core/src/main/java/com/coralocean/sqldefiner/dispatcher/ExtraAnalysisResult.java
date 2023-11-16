package com.coralocean.sqldefiner.dispatcher;

public class ExtraAnalysisResult {
    private String extra;
    private String level = ExplainLevel.INFO.getLevel();
    private String extraDescription;

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getExtraDescription() {
        return extraDescription;
    }

    public void setExtraDescription(String extraDescription) {
        this.extraDescription = extraDescription;
    }
}
