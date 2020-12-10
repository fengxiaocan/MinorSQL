package com.app.msql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.app.annotation.SQLite;
import com.app.data.MISQLSupport;

import java.util.List;

public class WhereHandler extends MinorWhere {

    public WhereHandler(String databaseName, String table) {
        super(databaseName, table);
    }

    public void deleteOrThrow() {
        execSQL(buildDeleteString(),selectionArgs());
    }

    public void delete() {
        try {
            deleteOrThrow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void updateOrThrow(T data) {
        if (data == null) return;
        SQLiteDatabase database = null;
        try {
            database = getDatabase();
            database.execSQL(buildUpdateString((MISQLSupport) data));
        } finally {
            SQLiteUtils.closeDatabase(database);
        }
    }

    public <T> void update(T data) {
        try {
            updateOrThrow(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void execSQL(String sql, Object[] bindArgs) {
        SQLiteDatabase database = null;
        try {
            database = getDatabase();
            if (bindArgs == null){
                database.execSQL(sql);
            }else {
                database.execSQL(sql, bindArgs);
            }
        } finally {
            SQLiteUtils.closeDatabase(database);
        }
    }

    private String buildUpdateString(MISQLSupport support) {
        StringBuilder query = new StringBuilder(120);
        query.append("UPDATE ");
        if (TextUtils.isEmpty(table)) {
            query.append(support.defaultTableName());
        } else {
            query.append(table);
        }
        query.append(" SET ");
        ContentValues value = new ContentValues();
        support.SQLiteValues(value);
        int index = 0;
        for (String key : value.keySet()) {
            query.append(key);
            query.append(" = '");
            query.append(value.get(key));

            index++;
            if (index < value.size()) {
                query.append("', ");
            } else {
                query.append("' ");
            }
        }

        if (whereBuilder != null && whereBuilder.length() > 0) {
            query.append("WHERE ");
            query.append(whereBuilder.toString());
        } else {
            query.append("WHERE ");
            query.append(SQLite.SQL_ID);
            query.append(" = ");
            query.append(support.getSQLiteID());
        }
        return query.toString();
    }

    private String buildDeleteString() {
        //DELETE FROM table_name
        StringBuilder query = new StringBuilder(120);
        query.append("DELETE FROM ");
        query.append(table);
        if (whereBuilder != null && whereBuilder.length() > 0) {
            query.append(" WHERE ");
            query.append(whereBuilder.toString());
        }
        return query.toString();
    }


    public AsyncExecutor deleteAsync() {
        AsyncExecutor executor = new AsyncExecutor();
        executor.submit(() -> {
            synchronized (Object.class) {
                delete();
                executor.call();
            }
        });
        return executor;
    }

    public <T> AsyncExecutor updateOrThrowAsync(T data) {
        AsyncExecutor executor = new AsyncExecutor();
        executor.submit(() -> {
            synchronized (Object.class) {
                updateOrThrow(data);
                executor.call();
            }
        });
        return executor;
    }

    public <T> AsyncExecutor updateAsync(T data) {
        AsyncExecutor executor = new AsyncExecutor();
        executor.submit(() -> {
            synchronized (Object.class) {
                update(data);
                executor.call();
            }
        });
        return executor;
    }


    /**
     * 构建 AND WHERE SQl 语句
     *
     * @param whereClause 查询语句,需要自己添加?替代符: column = ?
     * @param condition   查询条件的值,大小不能少于?的数量
     * @return
     */
    @Override
    public WhereHandler whereSql(String whereClause, Object... condition) {
        return (WhereHandler)super.whereSql(whereClause, condition);
    }

    /**
     * 构建 OR WHERE SQl 语句
     *
     * @param whereClause 查询语句,需要自己添加?替代符: column = ?
     * @param condition   查询条件的值,大小不能少于?的数量
     * @return
     */
    @Override
    public WhereHandler orWhereSql(String whereClause, Object... condition) {
        return (WhereHandler)super.orWhereSql(whereClause, condition);
    }

    /**
     * 构建 AND column IN(?,?...?) 查询条件语句
     *
     * @param column     查询的行的名称
     * @param conditions 查询条件值
     * @return
     */
    @Override
    public WhereHandler whereIn(String column, Object... conditions) {
        return (WhereHandler)super.whereIn(column, conditions);
    }

    /**
     * 构建 OR column IN(?,?...?) 查询条件语句
     *
     * @param column     查询的行的名称
     * @param conditions 查询条件值
     * @return
     */
    @Override
    public WhereHandler orWhereIn(String column, Object... conditions) {
        return (WhereHandler)super.orWhereIn(column, conditions);
    }

    /**
     * 构建 AND column IN(?,?...?) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param list   查询条件值的集合
     * @return
     */
    @Override
    public WhereHandler whereIn(String column, List<Object> list) {
        return (WhereHandler)super.whereIn(column, list);
    }

    /**
     * 构建 OR column IN(?,?...?) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param list   查询条件值的集合
     * @return
     */
    @Override
    public WhereHandler orWhereIn(String column, List<Object> list) {
        return (WhereHandler)super.orWhereIn(column, list);
    }

    /**
     * 构建以ID为查询条件语句
     *
     * @param ids 查询的ID值
     * @return
     */
    @Override
    public WhereHandler whereId(long... ids) {
        return (WhereHandler)super.whereId(ids);
    }

    /**
     * 构建 AND column LIKE ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler whereLike(String column, Object condition) {
        return (WhereHandler)super.whereLike(column, condition);
    }

    /**
     * 构建 AND column = ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler whereEqual(String column, Object condition) {
        return (WhereHandler)super.whereEqual(column, condition);
    }

    /**
     * 构建 AND column != ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler whereNoEqual(String column, Object condition) {
        return (WhereHandler)super.whereNoEqual(column, condition);
    }

    /**
     * 构建 AND column > ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler whereGreater(String column, Object condition) {
        return (WhereHandler)super.whereGreater(column, condition);
    }

    /**
     * 构建 AND column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler whereLess(String column, Object condition) {
        return (WhereHandler)super.whereLess(column, condition);
    }

    /**
     * 构建 AND column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler whereNoGreater(String column, Object condition) {
        return (WhereHandler)super.whereNoGreater(column, condition);
    }

    /**
     * 构建 AND column >= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler whereNoLess(String column, Object condition) {
        return (WhereHandler)super.whereNoLess(column, condition);
    }

    /**
     * 构建 OR column LIKE ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler orWhereLike(String column, Object condition) {
        return (WhereHandler)super.orWhereLike(column, condition);
    }

    /**
     * 构建 OR column = ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler orWhereEqual(String column, Object condition) {
        return (WhereHandler)super.orWhereEqual(column, condition);
    }

    /**
     * 构建 OR column != ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler orWhereNoEqual(String column, Object condition) {
        return (WhereHandler)super.orWhereNoEqual(column, condition);
    }

    /**
     * 构建 OR column > ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler orWhereGreater(String column, Object condition) {
        return (WhereHandler)super.orWhereGreater(column, condition);
    }

    /**
     * 构建 OR column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler orWhereLess(String column, Object condition) {
        return (WhereHandler)super.orWhereLess(column, condition);
    }

    /**
     * 构建 OR column <= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler orWhereNoGreater(String column, Object condition) {
        return (WhereHandler)super.orWhereNoGreater(column, condition);
    }

    /**
     * 构建 OR column >= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public WhereHandler orWhereNoLess(String column, Object condition) {
        return (WhereHandler)super.orWhereNoLess(column, condition);
    }
}
