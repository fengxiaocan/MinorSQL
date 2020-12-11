package com.app.msql;

import android.database.sqlite.SQLiteDatabase;

class MinorWhere extends SQLiteWhere {
    protected String databaseName;

    public MinorWhere(String databaseName, String table) {
        super(table);
        this.databaseName = databaseName;
    }

    protected SQLiteDatabase getDatabase() {
        return MSQLHelper.getSQLiteHelper(databaseName).getWritableDatabase();
    }

    private static SQLiteWhere whereMax(String table, String column) {
        SQLiteWhere liteWhere = new SQLiteWhere();
        liteWhere.whereBuilder = new StringBuilder();
        liteWhere.whereBuilder.append("SELECT MAX(");
        liteWhere.whereBuilder.append(column);
        liteWhere.whereBuilder.append(") FROM ");
        liteWhere.whereBuilder.append(table);
        return liteWhere;
    }

    private static SQLiteWhere whereMin(String table, String column) {
        SQLiteWhere liteWhere = new SQLiteWhere();
        liteWhere.whereBuilder = new StringBuilder();
        liteWhere.whereBuilder.append("SELECT MIN(");
        liteWhere.whereBuilder.append(column);
        liteWhere.whereBuilder.append(") FROM ");
        liteWhere.whereBuilder.append(table);
        return liteWhere;
    }

    /**
     * 构建 查询字段中最大值的 SQL语句
     *
     * @param column
     * @return
     */
    public SQLiteWhere Max(String column) {
        return EqualBracket(column, whereMax(table, column));
    }

    /**
     * 构建 查询字段中最小值的 SQL语句
     *
     * @param column
     * @return
     */
    public SQLiteWhere Min(String column) {
        return EqualBracket(column, whereMin(table, column));
    }

}
