package com.coralocean.sqldefiner.dispatcher;


import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class ExplainExecutor {

    private static final Set<String> EXPLAIN_WARN_ACCESSTYPE = Arrays.stream(new String[]{"ALL"}).collect(Collectors.toSet());
    private static final Set<String> EXPLAIN_WARN_EXTRA = Arrays.stream(new String[]{"Using temporary", "Using filesort"}).collect(Collectors.toSet());

    private static final Set<String> EXPLAIN_KEY_WORDS = Arrays.stream(new String[]{
            "id",
            "access_type",
            "attached_condition",
            "attached_subqueries",
            "buffer_result",
            "cacheable",
            "cost_info",
            "data_read_per_join",
            "dependent",
            "duplicates_removal",
            "eval_cost",
            "filtered",
            "group_by_subqueries",
            "grouping_operation",
            "having_subqueries",
            "key",
            "key_length",
            "materialized_from_subquery",
            "message",
            "nested_loop",
            "optimized_away_subqueries",
            "order_by_subqueries",
            "ordering_operation",
            "possible_keys",
            "prefix_cost",
            "query_block",
            "query_cost",
            "query_specifications",
            "read_cost",
            "ref",
            "rows_examined_per_scan",
            "rows_produced_per_join",
            "select_id",
            "select_list_subqueries",
            "sort_cost",
            "table",
            "table_name",
            "union_result",
            "update_value_subqueries",
            "used_columns",
            "used_key_parts",
            "using_filesort",
            "using_index",
            "using_index_for_group_by",
            "using_temporary_table",
    }).collect(Collectors.toSet());

    private static final Map<String, String> EXPLAIN_SELECT_TYPE = Arrays.stream(new String[][]{
                    {"SIMPLE", "简单SELECT(不使用UNION或子查询等)."},
                    {"PRIMARY", "最外层的select."},
                    {"UNION", "UNION中的第二个或后面的SELECT查询, 不依赖于外部查询的结果集."},
                    {"DEPENDENT", "UNION中的第二个或后面的SELECT查询, 依赖于外部查询的结果集."},
                    {"UNION RESULT", "UNION查询的结果集."},
                    {"SUBQUERY", "子查询中的第一个SELECT查询, 不依赖于外部查询的结果集."},
                    {"DEPENDENT SUBQUERY", "子查询中的第一个SELECT查询, 依赖于外部查询的结果集."},
                    {"DERIVED", "用于from子句里有子查询的情况. MySQL会递归执行这些子查询, 把结果放在临时表里."},
                    {"MATERIALIZED", "Materialized subquery."},
                    {"UNCACHEABLE SUBQUERY", "结果集不能被缓存的子查询, 必须重新为外层查询的每一行进行评估."},
                    {"UNCACHEABLE UNION", "UNION中的第二个或后面的select查询, 属于不可缓存的子查询（类似于UNCACHEABLE SUBQUERY）."}
            })
            .collect(Collectors.toMap(a -> a[0], b -> b[1]));

    private static final Map<String, String> EXPLAIN_ACESS_TYPE = Arrays.stream(new String[][]{
            {"system", "这是const连接类型的一种特例, 该表仅有一行数据(=系统表)."},
            {"const", "const用于使用常数值比较PRIMARY KEY时, 当查询的表仅有一行时, 使用system. 例,SELECT * FROM tbl WHERE col = 1."},
            {"eq_ref", "除const类型外最好的可能实现的连接类型. 它用在一个索引的所有部分被连接使用并且索引是UNIQUE或PRIMARY KEY, 对于每个索引键, 表中只有一条记录与之匹配. 例, 'SELECT * FROM RefTbl, tbl WHERE RefTbl.col=tbl.col;'."},
            {"ref", "连接不能基于关键字选择单个行, 可能查找到多个符合条件的行. 叫做ref是因为索引要跟某个参考值相比较. 这个参考值或者是一个数, 或者是来自一个表里的多表查询的结果值. 例,'SELECT * FROM tbl WHERE idx_col=expr;'."},
            {"fulltext", "查询时使用 FULLTEXT 索引."},
            {"ref_or_null", "如同ref, 但是MySQL必须在初次查找的结果里找出null条目, 然后进行二次查找."},
            {"index_merge", "表示使用了索引合并优化方法. 在这种情况下. key列包含了使用的索引的清单, key_len包含了使用的索引的最长的关键元素. 详情请见 8.2.1.4, “Index Merge Optimization”."},
            {"unique_subquery", "在某些IN查询中使用此种类型，而不是常规的ref,'value IN (SELECT PrimaryKey FROM SingleTable WHERE SomeExpr)'."},
            {"index_subquery", "在某些IN查询中使用此种类型, 与 unique_subquery 类似, 但是查询的是非唯一索引性索引."},
            {"range", "只检索给定范围的行, 使用一个索引来选择行. key列显示使用了哪个索引. key_len包含所使用索引的最长关键元素."},
            {"index", "全表扫描, 只是扫描表的时候按照索引次序进行而不是行. 主要优点就是避免了排序, 但是开销仍然非常大."},
            {"ALL", "最坏的情况, 从头到尾全表扫描."},
    }).collect(Collectors.toMap(a -> a[0], b -> b[1]));

    private static final Map<String, String> EXPLAIN_SCALABILITY = Arrays.stream(new String[][]{
            {"NULL", "NULL"},
            {"null", "NULL"},
            {"ALL", "O(n)"},
            {"index", "O(n)"},
            {"range", "O(log n)"},
            {"index_subquery", "O(log n)"},
            {"unique_subquery", "O(log n)"},
            {"index_merge", "O(log n)"},
            {"ref_or_null", "O(log n)"},
            {"fulltext", "O(log n)"},
            {"ref", "O(log n)"},
            {"eq_ref", "O(log n)"},
            {"const", "O(1)"},
            {"system", "O(1)"},
    }).collect(Collectors.toMap(a -> a[0], b -> b[1]));

    private static final Map<String, String> EXPLAN_EXTRA = Arrays.stream(new String[][]{
            {"Using temporary", "表示MySQL在对查询结果排序时使用临时表. 常见于排序order by和分组查询group by."},
            {"Using filesort", "MySQL会对结果使用一个外部索引排序,而不是从表里按照索引次序读到相关内容. 可能在内存或者磁盘上进行排序. MySQL中无法利用索引完成的排序操作称为'文件排序'."},
            {"Using index condition", "在5.6版本后加入的新特性（Index Condition Pushdown）。Using index condition 会先条件过滤索引，过滤完索引后找到所有符合索引条件的数据行，随后用 WHERE 子句中的其他条件去过滤这些数据行。"},
            {"Range checked for each record", "MySQL没有发现好的可以使用的索引,但发现如果来自前面的表的列值已知,可能部分索引可以使用。"},
            {"Using where with pushed condition", "这是一个仅仅在NDBCluster存储引擎中才会出现的信息，打开condition pushdown优化功能才可能被使用。"},
            {"Using MRR", "使用了 MRR Optimization IO 层面进行了优化，减少 IO 方面的开销。"},
            {"Skip_open_table", "Tables are read using the Multi-Range Read optimization strategy."},
            {"Open_frm_only", "Table files do not need to be opened. The information is already available from the data dictionary."},
            {"Open_full_table", "Unoptimized information lookup. Table information must be read from the data dictionary and by reading table files."},
            {"Scanned", "This indicates how many directory scans the server performs when processing a query for INFORMATION_SCHEMA tables."},
            {"Using index for group-by", "Similar to the Using index table access method, Using index for group-by indicates that MySQL found an index that can be used to retrieve all columns of a GROUP BY or DISTINCT query without any extra disk access to the actual table. Additionally, the index is used in the most efficient way so that for each group, only a few index entries are read."},
            {"Start temporary", "This indicates temporary table use for the semi-join Duplicate Weedout strategy.Start"},
            {"End temporary", "This indicates temporary table use for the semi-join Duplicate Weedout strategy.End"},
            {"FirstMatch", "The semi-join FirstMatch join shortcutting strategy is used for tbl_name."},
            {"Materialize", "Materialized subquery"},
            {"Start materialize", "Materialized subquery Start"},
            {"End materialize", "Materialized subquery End"},
            {"unique row not found", "For a query such as SELECT ... FROM tbl_name, no rows satisfy the condition for a UNIQUE index or PRIMARY KEY on the table."},
            // "Scan",                                                ""},
            // "Impossible ON condition",                             ""},
            // "Ft_hints,",                                           ""},
            // "Backward index scan",                                 ""},
            // "Recursive",                                           ""},
            // "Table function,",                                     ""},
            {"Index dive skipped due to FORCE", "This item applies to NDB tables only. It means that MySQL Cluster is using the Condition Pushdown optimization to improve the efficiency of a direct comparison between a nonindexed column and a constant. In such cases, the condition is “pushed down” to the cluster's data nodes and is evaluated on all data nodes simultaneously. This eliminates the need to send nonmatching rows over the network, and can speed up such queries by a factor of 5 to 10 times over cases where Condition Pushdown could be but is not used."},
            {"Impossible WHERE noticed after reading const tables", "查询了所有const(和system)表, 但发现WHERE查询条件不起作用."},
            {"Using where", "WHERE条件用于筛选出与下一个表匹配的数据然后返回给客户端. 除非故意做的全表扫描, 否则连接类型是ALL或者是index, 且在Extra列的值中没有Using Where, 则该查询可能是有问题的."},
            {"Using join buffer", "从已有连接中找被读入缓存的数据, 并且通过缓存来完成与当前表的连接."},
            {"Using index", "只需通过索引就可以从表中获取列的信息, 无需额外去读取真实的行数据. 如果查询使用的列值仅仅是一个简单索引的部分值, 则会使用这种策略来优化查询."},
            {"const row not found", "空表做类似 SELECT ... FROM tbl_name 的查询操作."},
            {"Distinct", "MySQL is looking for distinct values, so it stops searching for more rows for the current row combination after it has found the first matching row."},
            {"Full scan on NULL key", "子查询中的一种优化方式, 常见于无法通过索引访问null值."},
            {"Impossible HAVING", "HAVING条件过滤没有效果, 返回已有查询的结果集."},
            {"Impossible WHERE", "WHERE条件过滤没有效果, 最终是全表扫描."},
            {"LooseScan", "使用半连接LooseScan策略."},
            {"No matching min/max row", "没有行满足查询的条件, 如 SELECT MIN(...) FROM ... WHERE condition."},
            {"no matching row in const table", "对于连接查询, 列未满足唯一索引的条件或表为空."},
            {"No matching rows after partition pruning", "对于DELETE 或 UPDATE, 优化器在分区之后, 未发现任何要删除或更新的内容. 类似查询 Impossible WHERE."},
            {"No tables used", "查询没有FROM子句, 或者有一个 FROM DUAL子句."},
            {"Not exists", "MySQL能够对LEFT JOIN查询进行优化, 并且在查找到符合LEFT JOIN条件的行后, 则不再查找更多的行."},
            {"Plan isn't ready yet", "This value occurs with EXPLAIN FOR CONNECTION when the optimizer has not finished creating the execution plan for the statement executing in the named connection. If execution plan output comprises multiple lines, any or all of them could have this Extra value, depending on the progress of the optimizer in determining the full execution plan."},
            {"Select tables optimized away", "仅通过使用索引，优化器可能仅从聚合函数结果中返回一行。如：在没有 GROUP BY 子句的情况下，基于索引优化 MIN/MAX 操作，或者对于 MyISAM 存储引擎优化 COUNT(*) 操作，不必等到执行阶段再进行计算，查询执行计划生成的阶段即完成优化。"},
            {"Using intersect", "开启了index merge，即：对多个索引分别进行条件扫描，然后将它们各自的结果进行合并，使用的算法为：index_merge_intersection"},
            {"Using union", "开启了index merge，即：对多个索引分别进行条件扫描，然后将它们各自的结果进行合并，使用的算法为：index_merge_union"},
            {"Using sort_union", "开启了index merge，即：对多个索引分别进行条件扫描，然后将它们各自的结果进行合并，使用的算法为：index_merge_sort_union"}
    }).collect(Collectors.toMap(a -> a[0], b -> b[1]));

    public List<ExplainRow> formatJSONInfoToTraditional(List<Map<String, Object>> explainMaps) {
        List<ExplainRow> explainRows = new ArrayList<>();
        for (Map<String, Object> explainMap : explainMaps) {
            ExplainRow explainRow = new ExplainRow();
            explainRow.setId(Optional.ofNullable((BigInteger) explainMap.get("id")).map(BigInteger::longValue).orElse(null));
            explainRow.setSelectType((String) explainMap.get("select_type"));
            explainRow.setTable((String) explainMap.get("table"));
            explainRow.setPartitions((String) explainMap.get("partitions"));
            explainRow.setType((String) explainMap.get("type"));
            explainRow.setPossibleKeys(Optional.ofNullable((String) explainMap.get("possible_keys")).map(item -> item.split(",")).orElse(null));
            explainRow.setKey((String) explainMap.get("key"));
            explainRow.setKeyLen((String) explainMap.get("key_len"));
            explainRow.setRef(Optional.ofNullable((String) explainMap.get("ref")).map(item -> item.split(",")).orElse(null));
            explainRow.setRows(Optional.ofNullable((BigInteger) explainMap.get("rows")).map(BigInteger::longValue).orElse(null));
            explainRow.setExtra((String) explainMap.get("Extra"));
            explainRow.setScalability(EXPLAIN_SCALABILITY.get(String.valueOf(explainRow.getType())));
            explainRows.add(explainRow);
        }
        return explainRows;
    }

    public AnalysisResult explainInfoTranslator(ExplainRow explainRow) {
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setExplainRow(explainRow);
        Optional<SelectTypeAnalysisResult> selectTypeAnalysisResult = analysisSelectType(explainRow);
        selectTypeAnalysisResult.ifPresent(analysisResult::setSelectTypeAnalysisResult);
        Optional<AccessTypeAnalysisResult> accessTypeAnalysisResult = analysisAccessTypeResult(explainRow);
        accessTypeAnalysisResult.ifPresent(analysisResult::setAccessTypeAnalysisResult);
        Optional<ExtraAnalysisResult> extraAnalysisResult = analysisExtraResult(explainRow);
        extraAnalysisResult.ifPresent(analysisResult::setExtraAnalysisResult);
        return analysisResult;

    }

    private Optional<SelectTypeAnalysisResult> analysisSelectType(ExplainRow explainRow) {
        String selectType = explainRow.getSelectType();
        if (EXPLAIN_SELECT_TYPE.containsKey(selectType)) {
            String selectTypeDesc = EXPLAIN_SELECT_TYPE.get(selectType);
            SelectTypeAnalysisResult selectTypeAnalysisResult = new SelectTypeAnalysisResult();
            selectTypeAnalysisResult.setSelectTypeDescription(String.format("%s: %s", selectType, selectTypeDesc));
            return Optional.of( selectTypeAnalysisResult );
        }
        return Optional.empty();
    }

    private Optional<AccessTypeAnalysisResult> analysisAccessTypeResult(ExplainRow explainRow) {
        String accessType = explainRow.getType();
        if (EXPLAIN_ACESS_TYPE.containsKey(accessType)) {
            AccessTypeAnalysisResult accessTypeAnalysisResult = new AccessTypeAnalysisResult();
            String accessTypeDesc = EXPLAIN_ACESS_TYPE.get(accessType);
            boolean waran = EXPLAIN_WARN_ACCESSTYPE.contains(accessType);
            if (waran) {
                accessTypeAnalysisResult.setLevel(ExplainLevel.WARNING.getLevel());
            }
            accessTypeAnalysisResult.setAccessDescription(String.format("%s: %s", explainRow.getType(), accessTypeDesc));
            accessTypeAnalysisResult.setAccessType(accessType);
            return Optional.of( accessTypeAnalysisResult );
        }
        return Optional.empty();
    }

    private Optional<ExtraAnalysisResult> analysisExtraResult(ExplainRow explainRow) {
        String extraInfo = explainRow.getExtra();
        if (extraInfo != null) {
            String[] extras = extraInfo.split(";\\s+");
            ExtraAnalysisResult extraAnalysisResult = new ExtraAnalysisResult();
            StringBuilder desc = new StringBuilder();
            for (String extra : extras) {
                for (Map.Entry<String, String> extraEntry : EXPLAN_EXTRA.entrySet()) {
                    String extraEntryKey = extraEntry.getKey();
                    String extraDesc = extraEntry.getValue();
                    if (extra.contains(extraEntryKey)) {
                        if (extraEntryKey.equals("Impossible WHERE") && extra.contains("Impossible WHERE noticed after reading const tables")) {
                            continue;
                        }
                        if (extraEntryKey.equals("Using index") && extra.contains("Using index condition")) {
                            continue;
                        }
                        boolean warn = EXPLAIN_WARN_EXTRA.contains(extraEntryKey);
                        if (warn) {
                            extraAnalysisResult.setLevel(ExplainLevel.WARNING.getLevel());
                        }
                        desc.append(String.format("%s: %s", extraEntryKey, extraDesc)).append("\n");
                        break;
                    }
                }
            }
            extraAnalysisResult.setExtraDescription(desc.toString());
            return Optional.of(extraAnalysisResult);
        }
        return Optional.empty();
    }
}
