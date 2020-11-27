package com.app.msql;

public class MinorSQLHandler extends MinorSQL {

    public MinorSQLHandler(String databaseName) {
        super(databaseName);
    }

    public QueryHandler query() {
        return new QueryHandler(databaseName, tableName);
    }

    public WhereHandler asWhere() {
        return new WhereHandler(databaseName, tableName);
    }

    public QueryHandler asQuery() {
        return new QueryHandler(databaseName, tableName);
    }

    public int count() {
        return SQLiteUtils.count(databaseName, "SELECT * FROM " + tableName);
    }

    /**
     * 删除数据库表,删除后不会再自动创建,除非自动调用SQL语句来重新创建
     */
    public void deleteTable() {
        SQLiteUtils.deleteTable(databaseName, tableName);
    }

    /**
     * 清空数据库
     */
    public void clearTable() {
        SQLiteUtils.clearTable(databaseName, tableName);
    }


    /**
     * 给数据库表重命名
     */
    public void renameTable(String newTableName) {
        SQLiteUtils.renameTable(databaseName, tableName, newTableName);
    }
}
