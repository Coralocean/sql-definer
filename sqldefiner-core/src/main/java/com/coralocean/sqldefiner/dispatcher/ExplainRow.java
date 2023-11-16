package com.coralocean.sqldefiner.dispatcher;

public class ExplainRow {
    private Long id;
    private String selectType;
    private String table;
    private String partitions;
    private String type;
    private String[] possibleKeys;
    private String key;
    private String keyLen;
    private String[] ref;
    private Long rows;
    private Double filtered;
    private String scalability;
    private String extra;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPartitions() {
        return partitions;
    }

    public void setPartitions(String partitions) {
        this.partitions = partitions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getPossibleKeys() {
        return possibleKeys;
    }

    public void setPossibleKeys(String[] possibleKeys) {
        this.possibleKeys = possibleKeys;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyLen() {
        return keyLen;
    }

    public void setKeyLen(String keyLen) {
        this.keyLen = keyLen;
    }

    public String[] getRef() {
        return ref;
    }

    public void setRef(String[] ref) {
        this.ref = ref;
    }

    public Long getRows() {
        return rows;
    }

    public void setRows(Long rows) {
        this.rows = rows;
    }

    public Double getFiltered() {
        return filtered;
    }

    public void setFiltered(Double filtered) {
        this.filtered = filtered;
    }

    public String getScalability() {
        return scalability;
    }

    public void setScalability(String scalability) {
        this.scalability = scalability;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
