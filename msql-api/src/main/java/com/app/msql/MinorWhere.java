package com.app.msql;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.app.annotation.SQLite;

import java.util.ArrayList;
import java.util.List;

class MinorWhere {
    protected String databaseName;
    protected String table;

    protected StringBuilder whereBuilder;
    protected List<Object> selectionArgs;

    public MinorWhere(String databaseName, String table) {
        this.databaseName = databaseName;
        this.table = table;
    }

    protected static void appendColumns(StringBuilder s, String[] columns) {
        int n = columns.length;
        for (int i = 0; i < n; i++) {
            String column = columns[i];
            if (column != null) {
                if (i > 0) {
                    s.append(", ");
                }
                s.append(column);
            }
        }
        s.append(" ");
    }

    protected static void appendClause(StringBuilder s, String name, String clause) {
        if (!TextUtils.isEmpty(clause)) {
            s.append(name);
            s.append(clause);
        }
    }

    /**
     * 构建 AND WHERE SQl 语句
     *
     * @param whereClause 查询语句,需要自己添加?替代符: column = ?
     * @param condition   查询条件的值,大小不能少于?的数量
     * @return
     */
    public MinorWhere whereSql(String whereClause, Object... condition) {
        where(false, whereClause);
        addArgs(condition);
        return this;
    }

    /**
     * 构建 OR WHERE SQl 语句
     *
     * @param whereClause 查询语句,需要自己添加?替代符: column = ?
     * @param condition   查询条件的值,大小不能少于?的数量
     * @return
     */
    public MinorWhere orWhereSql(String whereClause, Object... condition) {
        where(true, whereClause);
        addArgs(condition);
        return this;
    }

    /**
     * 构建 AND column IN(?,?...?) 查询条件语句
     *
     * @param column     查询的行的名称
     * @param conditions 查询条件值
     * @return
     */
    public MinorWhere whereIn(String column, Object... conditions) {
        return whereIn(false, column, conditions);
    }

    /**
     * 构建 OR column IN(?,?...?) 查询条件语句
     *
     * @param column     查询的行的名称
     * @param conditions 查询条件值
     * @return
     */
    public MinorWhere orWhereIn(String column, Object... conditions) {
        return whereIn(true, column, conditions);
    }

    /**
     * 构建 AND column IN(?,?...?) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param list   查询条件值的集合
     * @return
     */
    public MinorWhere whereIn(String column, List<Object> list) {
        return whereIn(false, column, list);
    }

    /**
     * 构建 OR column IN(?,?...?) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param list   查询条件值的集合
     * @return
     */
    public MinorWhere orWhereIn(String column, List<Object> list) {
        return whereIn(true, column, list);
    }

    /**
     * 构建以ID为查询条件语句
     *
     * @param ids 查询的ID值
     * @return
     */
    public MinorWhere whereId(long... ids) {
        where(false, SQLite.SQL_ID, " IN (");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                whereBuilder.append(",");
            }
            whereBuilder.append(ids[i]);
        }
        whereBuilder.append(")");
        return this;
    }

    /**
     * 构建 AND column LIKE ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere whereLike(String column, Object condition) {
        where(false, column, " LIKE ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 AND column = ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere whereEqual(String column, Object condition) {
        where(false, column, " = ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 AND column != ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere whereNoEqual(String column, Object condition) {
        where(false, column, " != ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 AND column > ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere whereGreater(String column, Object condition) {
        where(false, column, " > ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 AND column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere whereLess(String column, Object condition) {
        where(false, column, " < ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 AND column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere whereNoGreater(String column, Object condition) {
        where(false, column, " <= ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 AND column >= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere whereNoLess(String column, Object condition) {
        where(false, column, " >= ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 OR column LIKE ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere orWhereLike(String column, Object condition) {
        where(true, column, " LIKE ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 OR column = ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere orWhereEqual(String column, Object condition) {
        where(true, column, " = ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 OR column != ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere orWhereNoEqual(String column, Object condition) {
        where(true, column, " != ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 OR column > ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere orWhereGreater(String column, Object condition) {
        where(true, column, " > ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 OR column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere orWhereLess(String column, Object condition) {
        where(true, column, " < ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 OR column <= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere orWhereNoGreater(String column, Object condition) {
        where(true, column, " <= ?");
        addArgs(condition);
        return this;
    }

    /**
     * 构建 OR column >= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public MinorWhere orWhereNoLess(String column, Object condition) {
        where(true, column, " >= ?");
        addArgs(condition);
        return this;
    }

    protected SQLiteDatabase getDatabase() {
        return MSQLHelper.getSQLiteHelper(databaseName).getWritableDatabase();
    }

    protected MinorWhere where(boolean isOr, String... conditions) {
        if (whereBuilder == null) {
            whereBuilder = new StringBuilder();
        } else {
            if (whereBuilder.length() > 0) {
                if (isOr) whereBuilder.append(" OR ");
                else whereBuilder.append(" AND ");
            }
        }
        for (String value : conditions) {
            whereBuilder.append(value);
        }
        return this;
    }

    private void checkArgs() {
        if (selectionArgs == null) {
            selectionArgs = new ArrayList<>();
        }
    }

    private void addArgs(Object conditions) {
        checkArgs();
        selectionArgs.add(conditions);
    }

    private MinorWhere whereIn(boolean isOr, String column, Object... conditions) {
        where(isOr, column, " IN (");
        checkArgs();
        for (int i = 0; i < conditions.length; i++) {
            if (i > 0) {
                whereBuilder.append(",");
            }
            whereBuilder.append("?");
            selectionArgs.add(conditions[i]);
        }
        whereBuilder.append(")");
        return this;
    }

    private MinorWhere whereIn(boolean isOr, String column, List<Object> list) {
        where(isOr, column, " IN (");
        checkArgs();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                whereBuilder.append(",");
            }
            whereBuilder.append("?");
            selectionArgs.add(list.get(i));
        }
        whereBuilder.append(")");
        return this;
    }

    public String[] selectionArgs() {
        if (selectionArgs != null){
            String[] args = new String[selectionArgs.size()];
            for (int i = 0; i < selectionArgs.size(); i++) {
                Object obj = selectionArgs.get(i);
                if (obj != null){
                    args[i] = String.valueOf(obj);
                }
            }
            return args;
        }
        return null;
    }
}
