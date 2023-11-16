package com.coralocean.sqldefiner.dispatcher;


public class AnalysisResult {
    private SelectTypeAnalysisResult selectTypeAnalysisResult;
    private AccessTypeAnalysisResult accessTypeAnalysisResult;
    private ExtraAnalysisResult extraAnalysisResult;
    private ExplainRow explainRow;

    public SelectTypeAnalysisResult getSelectTypeAnalysisResult() {
        return selectTypeAnalysisResult;
    }

    public void setSelectTypeAnalysisResult(SelectTypeAnalysisResult selectTypeAnalysisResult) {
        this.selectTypeAnalysisResult = selectTypeAnalysisResult;
    }

    public AccessTypeAnalysisResult getAccessTypeAnalysisResult() {
        return accessTypeAnalysisResult;
    }

    public void setAccessTypeAnalysisResult(AccessTypeAnalysisResult accessTypeAnalysisResult) {
        this.accessTypeAnalysisResult = accessTypeAnalysisResult;
    }

    public ExtraAnalysisResult getExtraAnalysisResult() {
        return extraAnalysisResult;
    }

    public void setExtraAnalysisResult(ExtraAnalysisResult extraAnalysisResult) {
        this.extraAnalysisResult = extraAnalysisResult;
    }

    public ExplainRow getExplainRow() {
        return explainRow;
    }

    public void setExplainRow(ExplainRow explainRow) {
        this.explainRow = explainRow;
    }
}
