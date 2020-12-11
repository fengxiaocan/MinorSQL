package com.app.msql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.app.annotation.SQLite;
import com.app.data.MISQLSupport;

import java.util.List;

public class OperationHandler extends MinorWhere {

    public OperationHandler(String databaseName, String table) {
        super(databaseName, table);
    }

    public void deleteOrThrow() {
        execSQL(buildDeleteString(), selectionArgs());
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


    @Override
    public OperationHandler AndBracket(SQLiteWhere where) {
        return (OperationHandler) super.AndBracket(where);
    }

    @Override
    public OperationHandler OrBracket(SQLiteWhere where) {
        return (OperationHandler) super.OrBracket(where);
    }

    @Override
    public OperationHandler whereSql(String whereClause, Object... condition) {
        return (OperationHandler) super.whereSql(whereClause, condition);
    }

    @Override
    public OperationHandler orWhereSql(String whereClause, Object... condition) {
        return (OperationHandler) super.orWhereSql(whereClause, condition);
    }

    @Override
    public OperationHandler In(String column, Object... conditions) {
        return (OperationHandler) super.In(column, conditions);
    }

    @Override
    public OperationHandler OrIn(String column, Object... conditions) {
        return (OperationHandler) super.OrIn(column, conditions);
    }

    @Override
    public OperationHandler NotIn(String column, Object... conditions) {
        return (OperationHandler) super.NotIn(column, conditions);
    }

    @Override
    public OperationHandler OrNotIn(String column, Object... conditions) {
        return (OperationHandler) super.OrNotIn(column, conditions);
    }

    @Override
    public OperationHandler In(String column, List<Object> list) {
        return (OperationHandler) super.In(column, list);
    }

    @Override
    public OperationHandler OrIn(String column, List<Object> list) {
        return (OperationHandler) super.OrIn(column, list);
    }

    @Override
    public OperationHandler NotIn(String column, List<Object> list) {
        return (OperationHandler) super.NotIn(column, list);
    }

    @Override
    public OperationHandler OrNotIn(String column, List<Object> list) {
        return (OperationHandler) super.OrNotIn(column, list);
    }

    @Override
    public OperationHandler InSqlID(long... ids) {
        return (OperationHandler) super.InSqlID(ids);
    }

    @Override
    public OperationHandler Like(String column, String condition) {
        return (OperationHandler) super.Like(column, condition);
    }

    @Override
    public OperationHandler Glob(String column, String condition) {
        return (OperationHandler) super.Glob(column, condition);
    }

    @Override
    public OperationHandler StartWith(String column, String condition) {
        return (OperationHandler) super.StartWith(column, condition);
    }

    @Override
    public OperationHandler EndWith(String column, String condition) {
        return (OperationHandler) super.EndWith(column, condition);
    }

    @Override
    public OperationHandler Contain(String column, String condition) {
        return (OperationHandler) super.Contain(column, condition);
    }

    @Override
    public OperationHandler Equal(String column, Object condition) {
        return (OperationHandler) super.Equal(column, condition);
    }

    @Override
    public OperationHandler NoEqual(String column, Object condition) {
        return (OperationHandler) super.NoEqual(column, condition);
    }

    @Override
    public OperationHandler Greater(String column, Object condition) {
        return (OperationHandler) super.Greater(column, condition);
    }

    @Override
    public OperationHandler Less(String column, Object condition) {
        return (OperationHandler) super.Less(column, condition);
    }

    @Override
    public OperationHandler NoGreater(String column, Object condition) {
        return (OperationHandler) super.NoGreater(column, condition);
    }

    @Override
    public OperationHandler NoLess(String column, Object condition) {
        return (OperationHandler) super.NoLess(column, condition);
    }

    @Override
    public OperationHandler Between(String column, Object condition1, Object condition2) {
        return (OperationHandler) super.Between(column, condition1, condition2);
    }

    @Override
    public OperationHandler IsNull(String column) {
        return (OperationHandler) super.IsNull(column);
    }

    @Override
    public OperationHandler IsNotNull(String column) {
        return (OperationHandler) super.IsNotNull(column);
    }

    @Override
    public OperationHandler EqualBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.EqualBracket(column, where);
    }

    @Override
    public OperationHandler NoEqualBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.NoEqualBracket(column, where);
    }

    @Override
    public OperationHandler GreaterBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.GreaterBracket(column, where);
    }

    @Override
    public OperationHandler LessBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.LessBracket(column, where);
    }

    @Override
    public OperationHandler NoGreaterBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.NoGreaterBracket(column, where);
    }

    @Override
    public OperationHandler NoLessBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.NoLessBracket(column, where);
    }

    @Override
    public OperationHandler BetweenBracket(String column, SQLiteWhere where1, SQLiteWhere where2) {
        return (OperationHandler) super.BetweenBracket(column, where1, where2);
    }

    @Override
    public OperationHandler ExistsBracket(SQLiteWhere where) {
        return (OperationHandler) super.ExistsBracket(where);
    }

    @Override
    public OperationHandler OrLike(String column, String condition) {
        return (OperationHandler) super.OrLike(column, condition);
    }

    @Override
    public OperationHandler OrEqual(String column, Object condition) {
        return (OperationHandler) super.OrEqual(column, condition);
    }

    @Override
    public OperationHandler OrNoEqual(String column, Object condition) {
        return (OperationHandler) super.OrNoEqual(column, condition);
    }

    @Override
    public OperationHandler OrGreater(String column, Object condition) {
        return (OperationHandler) super.OrGreater(column, condition);
    }

    @Override
    public OperationHandler OrLess(String column, Object condition) {
        return (OperationHandler) super.OrLess(column, condition);
    }

    @Override
    public OperationHandler OrNoGreater(String column, Object condition) {
        return (OperationHandler) super.OrNoGreater(column, condition);
    }

    @Override
    public OperationHandler OrNoLess(String column, Object condition) {
        return (OperationHandler) super.OrNoLess(column, condition);
    }

    @Override
    public OperationHandler OrBetween(String column, Object condition1, Object condition2) {
        return (OperationHandler) super.OrBetween(column, condition1, condition2);
    }

    @Override
    public OperationHandler OrIsNull(String column) {
        return (OperationHandler) super.OrIsNull(column);
    }

    @Override
    public OperationHandler OrIsNotNull(String column) {
        return (OperationHandler) super.OrIsNotNull(column);
    }

    @Override
    public OperationHandler OrEqualBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.OrEqualBracket(column, where);
    }

    @Override
    public OperationHandler OrNoEqualBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.OrNoEqualBracket(column, where);
    }

    @Override
    public OperationHandler OrGreaterBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.OrGreaterBracket(column, where);
    }

    @Override
    public OperationHandler OrLessBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.OrLessBracket(column, where);
    }

    @Override
    public OperationHandler OrNoGreaterBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.OrNoGreaterBracket(column, where);
    }

    @Override
    public OperationHandler OrNoLessBracket(String column, SQLiteWhere where) {
        return (OperationHandler) super.OrNoLessBracket(column, where);
    }

    @Override
    public OperationHandler OrBetweenBracket(String column, SQLiteWhere where1, SQLiteWhere where2) {
        return (OperationHandler) super.OrBetweenBracket(column, where1, where2);
    }

    @Override
    public OperationHandler OrExistsBracket(SQLiteWhere where) {
        return (OperationHandler) super.OrExistsBracket(where);
    }

    @Override
    public OperationHandler OrGlob(String column, String condition) {
        return (OperationHandler) super.OrGlob(column, condition);
    }

    @Override
    public OperationHandler OrStartWith(String column, String condition) {
        return (OperationHandler) super.OrStartWith(column, condition);
    }

    @Override
    public OperationHandler OrEndWith(String column, String condition) {
        return (OperationHandler) super.OrEndWith(column, condition);
    }

    @Override
    public OperationHandler OrContain(String column, String condition) {
        return (OperationHandler) super.OrContain(column, condition);
    }

    @Override
    public OperationHandler NotBetween(String column, Object condition1, Object condition2) {
        return (OperationHandler) super.NotBetween(column, condition1, condition2);
    }

    @Override
    public OperationHandler NotBetweenBracket(String column, SQLiteWhere where1, SQLiteWhere where2) {
        return (OperationHandler) super.NotBetweenBracket(column, where1, where2);
    }

    @Override
    public OperationHandler NotExistsBracket(SQLiteWhere where) {
        return (OperationHandler) super.NotExistsBracket(where);
    }

    @Override
    public OperationHandler OrNotBetween(String column, Object condition1, Object condition2) {
        return (OperationHandler) super.OrNotBetween(column, condition1, condition2);
    }

    @Override
    public OperationHandler OrNotBetweenBracket(String column, SQLiteWhere where1, SQLiteWhere where2) {
        return (OperationHandler) super.OrNotBetweenBracket(column, where1, where2);
    }

    @Override
    public OperationHandler OrNotExistsBracket(SQLiteWhere where) {
        return (OperationHandler) super.OrNotExistsBracket(where);
    }

    @Override
    public OperationHandler Max(String column) {
        return (OperationHandler) super.Max(column);
    }

    @Override
    public OperationHandler Min(String column) {
        return (OperationHandler) super.Min(column);
    }
}
