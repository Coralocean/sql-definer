package com.coralocean.sqldefiner.dispatcher;

public class AccessTypeAnalysisResult {
    private String accessType;
    private String level = ExplainLevel.INFO.getLevel();
    private String accessDescription;

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAccessDescription() {
        return accessDescription;
    }

    public void setAccessDescription(String accessDescription) {
        this.accessDescription = accessDescription;
    }
}
