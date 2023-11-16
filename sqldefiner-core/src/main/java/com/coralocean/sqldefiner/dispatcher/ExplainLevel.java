package com.coralocean.sqldefiner.dispatcher;

public enum ExplainLevel {


    INFO("info"),
    WARNING("warning"),
    ERROR("error"),

    ;

    private final String level;
    ExplainLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }
}
