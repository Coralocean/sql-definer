package com.coralocean.sqldefiner.dispatcher;

import org.springframework.jdbc.core.JdbcTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ExplainDispatcher {

    private final JdbcTemplate jdbcTemplate;
    private final TemplateEngine templateEngine;

    private final ExplainExecutor explainExecutor = new ExplainExecutor();
    private final String analysisPath;


    public ExplainDispatcher(DataSource dataSource, TemplateEngine templateEngine, String analysisPath) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.templateEngine = templateEngine;
        this.analysisPath = analysisPath;
    }

    private String decorateSql(String sql) {
        return "EXPLAIN " + sql;
    }

    public Map<String, Object> explain(String sql) {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(decorateSql(sql));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sql", sql);
        parameters.putAll(analysisExplain(maps));
        return parameters;



    }

    public void completeExplain( List<Map<String, Object>> model ) {
        Context context = new Context();
        context.setVariable("items", model);
        String html = templateEngine.process("analysis", context);
        writeFile(html, analysisPath);
    }

    public void writeFile(String html, String analysisPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(analysisPath))) {
            writer.write(html);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Map<String, Object> analysisExplain(List<Map<String, Object>> maps) {
        Map<String, Object> result = new HashMap<>();
        List<String> plans = new ArrayList<>();
        List<String> analysis = new ArrayList<>();
        List<ExplainRow> explainRows = explainExecutor.formatJSONInfoToTraditional(maps);
        for ( ExplainRow explainRow : explainRows ) {
            AnalysisResult analysisResult = explainExecutor.explainInfoTranslator(explainRow);
            String s = formatAnalysisResult(analysisResult).replaceAll("\n", "<br>");
            analysis.add(s);
            plans.add(formatExplainResult(analysisResult).replaceAll("\n", "<br>"));
        }
        result.put("plans", plans);
        result.put("analysis", analysis);
        return result;
    }

    private String formatExplainResult(AnalysisResult analysisResult) {
        ExplainRow explainRow = analysisResult.getExplainRow();
        StringBuilder explain = new StringBuilder();
        if ( explainRow.getId() != null ) {
            explain.append(String.format("id: %s\n", explainRow.getId()));
        }
        if ( explainRow.getSelectType() != null ){
            explain.append(String.format("select_type: %s\n", explainRow.getSelectType()));
        }
        if ( explainRow.getTable() != null ) {
            explain.append(String.format("table: %s\n", explainRow.getTable()));
        }
        if ( explainRow.getPartitions() != null ) {
            explain.append(String.format("partitions: %s\n", explainRow.getPartitions()));
        }
        if ( explainRow.getType() != null) {
            explain.append(String.format("type: %s\n", explainRow.getType()));
        }
        if ( explainRow.getPossibleKeys() != null) {
            explain.append(String.format("possible_keys: %s\n", String.join(",",explainRow.getPossibleKeys())));
        }
        if ( explainRow.getKey() != null ) {
            explain.append(String.format("key: %s\n", explainRow.getKey()));
        }
        if ( explainRow.getKeyLen() != null) {
            explain.append(String.format("key: %s\n", explainRow.getKeyLen()));
        }
        if ( explainRow.getRef() != null ){
            explain.append(String.format("ref: %s\n", String.join(",",explainRow.getRef())));
        }
        if ( explainRow.getRows() != null ) {
            explain.append(String.format("rows: %s\n", explainRow.getRows()));
        }
        if ( explainRow.getFiltered() != null ) {
            explain.append(String.format("filtered: %s\n", explainRow.getFiltered()));
        }
        if ( explainRow.getScalability() != null ) {
            explain.append(String.format("<div style=\"color: blue;\">scalability: %s</div>", explainRow.getScalability()));
        }
        if ( explainRow.getExtra() != null ) {
            explain.append(String.format("Extra: %s\n", explainRow.getExtra()));
        }
        return explain.toString();
    }

    private String formatAnalysisResult(AnalysisResult analysisResult) {
        StringBuilder format = new StringBuilder();
        SelectTypeAnalysisResult selectTypeAnalysisResult = analysisResult.getSelectTypeAnalysisResult();
        if ( selectTypeAnalysisResult != null ) {
            format.append(String.format("<h3>%s</h3>", "select_type解读: "));
            if (ExplainLevel.INFO.getLevel().equals( selectTypeAnalysisResult.getSelectType() )) {
                format.append(selectTypeAnalysisResult.getSelectTypeDescription()).append("\n");
            } else {
                format.append(selectTypeAnalysisResult.getSelectTypeDescription()).append("\n");
            }
        }
        AccessTypeAnalysisResult accessTypeAnalysisResult = analysisResult.getAccessTypeAnalysisResult();
        if ( accessTypeAnalysisResult != null ) {
            format.append(String.format("<h4>%s</h4>","type解读："));
            if (ExplainLevel.INFO.getLevel().equals( accessTypeAnalysisResult.getLevel() )) {
                format.append( accessTypeAnalysisResult.getAccessDescription()).append("\n");
            } else {
                format.append(String.format("<div style=\"color: red;\">%s</div>", accessTypeAnalysisResult.getAccessDescription())).append("\n");
            }
        }
        ExtraAnalysisResult extraAnalysisResult = analysisResult.getExtraAnalysisResult();
        if ( extraAnalysisResult != null){
            format.append(String.format("<h4>%s</h4>","Extra解读：" ));
            if (ExplainLevel.INFO.getLevel().equals( extraAnalysisResult.getLevel() )) {
                format.append( extraAnalysisResult.getExtraDescription()).append("\n");
            } else {
                format.append(String.format("<div style=\"color: red;\">%s</div>", extraAnalysisResult.getExtraDescription())).append("\n");
            }
        }

        return format.toString();
    }





}
