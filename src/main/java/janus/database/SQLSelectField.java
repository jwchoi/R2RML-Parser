package janus.database;

public class SQLSelectField {
    private String columnNameOrAlias;
    private String selectQuery;
    private int nullable; // 0: ResultSetMetaData.columnNoNulls, 1: ResultSetMetaData.columnNullable, 2: ResultSetMetaData.columnNullableUnknown
    private int sqlType;  // SQL type

    public SQLSelectField(String columnNameOrAlias, String selectQuery) {
        this.columnNameOrAlias = columnNameOrAlias;
        this.selectQuery = selectQuery;
    }

    public void setNullable(int nullable) { this.nullable = nullable; }
    public void setSqlType(int sqlType) { this.sqlType = sqlType; }

    public int getSqlType() { return sqlType; }
    public int getNullable() { return nullable; }
    public String getColumnNameOrAlias() { return columnNameOrAlias; }
}
