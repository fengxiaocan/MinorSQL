package com.app.msql;

import android.text.TextUtils;

import com.app.annotation.SQLite;

import java.util.ArrayList;
import java.util.List;

public class SQLiteWhere {
    protected StringBuilder whereBuilder;
    protected List<Object> selectionArgs;

    protected String table;
    private String select;

    protected SQLiteWhere() {
    }

    protected SQLiteWhere(String table) {
        this.table = table;
    }

    protected SQLiteWhere(String table, String select) {
        this.table = table;
        this.select = select;
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
     * 构建一个 SELECT column FORM table WHERE 的语句
     *
     * @param table
     * @param column
     * @return
     */
    public static SQLiteWhere SELECT(String table, String column) {
        return new SQLiteWhere(table, column);
    }

    /**
     * 构建一个普通的 WHERE 的语句
     *
     * @return
     */
    public static SQLiteWhere WHERE() {
        return new SQLiteWhere();
    }

    private SQLiteWhere where(boolean isOr, String... conditions) {
        checkBuilder(isOr);
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

    private void checkBuilder(boolean isOr) {
        if (whereBuilder == null) {
            whereBuilder = new StringBuilder();
        } else {
            if (whereBuilder.length() > 0) {
                if (isOr) {
                    whereBuilder.append(" OR ");
                } else {
                    whereBuilder.append(" AND ");
                }
            }
        }
    }

    private SQLiteWhere addArgs(Object conditions) {
        checkArgs();
        selectionArgs.add(conditions);
        return this;
    }

    private SQLiteWhere whereIn(boolean isOr, boolean isNot, String column, Object... conditions) {
        where(isOr, column, isNot ? " NOT IN (" : " IN (");
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

    private SQLiteWhere whereIn(boolean isOr, boolean isNot, String column, List<Object> list) {
        where(isOr, column, isNot ? " NOT IN (" : " IN (");
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


    /**
     * 获取替代参数的值
     *
     * @return
     */
    public String[] selectionArgs() {
        if (selectionArgs != null) {
            String[] args = new String[selectionArgs.size()];
            for (int i = 0; i < selectionArgs.size(); i++) {
                Object obj = selectionArgs.get(i);
                if (obj != null) {
                    args[i] = String.valueOf(obj);
                }
            }
            return args;
        }
        return null;
    }

    private SQLiteWhere bracket(SQLiteWhere where) {
        whereBuilder.append("(");
        if (where.table != null && where.select != null) {
            whereBuilder.append("SELECT ");
            whereBuilder.append(where.select);
            whereBuilder.append(" FROM ");
            whereBuilder.append(where.table);
            whereBuilder.append(" WHERE ");
        }
        if (where.whereBuilder != null) {
            whereBuilder.append(where.whereBuilder.toString());
        }
        whereBuilder.append(")");
        if (where.selectionArgs != null) {
            checkArgs();
            selectionArgs.addAll(where.selectionArgs);
        }
        return this;
    }

    private SQLiteWhere bracket(boolean isOr, SQLiteWhere where) {
        checkBuilder(isOr);
        return bracket(where);
    }

    /**
     * 构建 AND (SQl) 语句
     *
     * @param where
     * @return
     */
    public SQLiteWhere AndBracket(SQLiteWhere where) {
        return bracket(false, where);
    }
    /**
     * 构建 OR (SQl) 语句
     *
     * @param where
     * @return
     */
    public SQLiteWhere OrBracket(SQLiteWhere where) {
        return bracket(true, where);
    }


    /**
     * 构建 AND WHERE SQl 语句
     *
     * @param whereClause 查询语句,需要自己添加?替代符: column = ?
     * @param condition   查询条件的值,大小不能少于?的数量
     * @return
     */
    public SQLiteWhere whereSql(String whereClause, Object... condition) {
        return where(false, whereClause).addArgs(condition);
    }

    /**
     * 构建 OR WHERE SQl 语句
     *
     * @param whereClause 查询语句,需要自己添加?替代符: column = ?
     * @param condition   查询条件的值,大小不能少于?的数量
     * @return
     */
    public SQLiteWhere orWhereSql(String whereClause, Object... condition) {
        return where(true, whereClause).addArgs(condition);
    }

    /**
     * 构建 AND column IN(?,?...?) 查询条件语句
     *
     * @param column     查询的行的名称
     * @param conditions 查询条件值
     * @return
     */
    public SQLiteWhere In(String column, Object... conditions) {
        return whereIn(false, false, column, conditions);
    }

    /**
     * 构建 OR column IN(?,?...?) 查询条件语句
     *
     * @param column     查询的行的名称
     * @param conditions 查询条件值
     * @return
     */
    public SQLiteWhere OrIn(String column, Object... conditions) {
        return whereIn(true, false, column, conditions);
    }

    /**
     * 构建 AND column NOT IN(?,?...?) 查询条件语句
     *
     * @param column     查询的行的名称
     * @param conditions 查询条件值
     * @return
     */
    public SQLiteWhere NotIn(String column, Object... conditions) {
        return whereIn(false, true, column, conditions);
    }

    /**
     * 构建 OR column NOT IN(?,?...?) 查询条件语句
     *
     * @param column     查询的行的名称
     * @param conditions 查询条件值
     * @return
     */
    public SQLiteWhere OrNotIn(String column, Object... conditions) {
        return whereIn(true, true, column, conditions);
    }

    /**
     * 构建 AND column IN(?,?...?) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param list   查询条件值的集合
     * @return
     */
    public SQLiteWhere In(String column, List<Object> list) {
        return whereIn(false, false, column, list);
    }

    /**
     * 构建 OR column IN(?,?...?) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param list   查询条件值的集合
     * @return
     */
    public SQLiteWhere OrIn(String column, List<Object> list) {
        return whereIn(true, false, column, list);
    }

    /**
     * 构建 AND column NOT IN(?,?...?) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param list   查询条件值的集合
     * @return
     */
    public SQLiteWhere NotIn(String column, List<Object> list) {
        return whereIn(false, true, column, list);
    }

    /**
     * 构建 OR column NOT IN(?,?...?) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param list   查询条件值的集合
     * @return
     */
    public SQLiteWhere OrNotIn(String column, List<Object> list) {
        return whereIn(true, true, column, list);
    }

    /**
     * 构建以ID为查询条件语句
     *
     * @param ids 查询的ID值
     * @return
     */
    public SQLiteWhere InSqlID(long... ids) {
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
     * @param condition 查询条件值,条件值一般和通配符搭配使用,不匹配大小写:
     *                  % 任意多个字符,包含0个字符;
     *                  _ 任意单个字符;
     * @return
     */
    public SQLiteWhere Like(String column, String condition) {
        return where(false, column, " LIKE ?").addArgs(condition);
    }

    /**
     * 构建 AND column GLOB ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值,条件值一般和通配符搭配使用,匹配大小写:
     *                  * 任意多个字符,包含0个字符
     * @return
     */
    public SQLiteWhere Glob(String column, String condition) {
        return where(false, column, " GLOB ?").addArgs(condition);
    }

    /**
     * 构建 AND column GLOB ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值,条件值一般和通配符搭配使用,匹配大小写:
     *                  * 任意多个字符,包含0个字符
     * @return
     */
    public SQLiteWhere StartWith(String column, String condition) {
        return where(false, column, " GLOB ?").addArgs(condition + "%");
    }

    /**
     * 构建 AND column GLOB ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值,条件值一般和通配符搭配使用,匹配大小写:
     *                  * 任意多个字符,包含0个字符
     * @return
     */
    public SQLiteWhere EndWith(String column, String condition) {
        return where(false, column, " GLOB ?").addArgs("%" + condition);
    }

    /**
     * 构建 AND column GLOB ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值,条件值一般和通配符搭配使用,匹配大小写:
     *                  * 任意多个字符,包含0个字符
     * @return
     */
    public SQLiteWhere Contain(String column, String condition) {
        return where(false, column, " GLOB ?").addArgs("%" + condition + "%");
    }

    /**
     * 构建 AND column = ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere Equal(String column, Object condition) {
        return where(false, column, " = ?").addArgs(condition);
    }

    /**
     * 构建 AND column != ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere NoEqual(String column, Object condition) {
        return where(false, column, " != ?").addArgs(condition);
    }

    /**
     * 构建 AND column > ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere Greater(String column, Object condition) {
        return where(false, column, " > ?").addArgs(condition);
    }

    /**
     * 构建 AND column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere Less(String column, Object condition) {
        return where(false, column, " < ?").addArgs(condition);
    }

    /**
     * 构建 AND column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere NoGreater(String column, Object condition) {
        return where(false, column, " <= ?").addArgs(condition);
    }

    /**
     * 构建 AND column >= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere NoLess(String column, Object condition) {
        return where(false, column, " >= ?").addArgs(condition);
    }

    /**
     * 构建 AND column BETWEEN  ? AND ? 查询条件语句
     *
     * @param column     查询的行的名称
     * @param condition1 查询条件值1
     * @param condition2 查询条件值2
     * @return
     */
    public SQLiteWhere Between(String column, Object condition1, Object condition2) {
        return where(false, column, " BETWEEN ? AND ?").addArgs(condition1).addArgs(condition2);
    }

    /**
     * 构建 AND column IS NULL 查询条件语句
     *
     * @param column 查询的行的名称
     * @return
     */
    public SQLiteWhere IsNull(String column) {
        return where(false, column, " IS NULL");
    }

    /**
     * 构建 AND column IS NOT NULL 查询条件语句
     *
     * @param column 查询的行的名称
     * @return
     */
    public SQLiteWhere IsNotNull(String column) {
        return where(false, column, " IS NOT NULL");
    }


    /**
     * 构建 AND column = (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere EqualBracket(String column, SQLiteWhere where) {
        return where(false, column, " = ").bracket(where);
    }

    /**
     * 构建 AND column != (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere NoEqualBracket(String column, SQLiteWhere where) {
        return where(false, column, " != ").bracket(where);
    }

    /**
     * 构建 AND column > (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere GreaterBracket(String column, SQLiteWhere where) {
        return where(false, column, " > ").bracket(where);
    }

    /**
     * 构建 AND column < (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere LessBracket(String column, SQLiteWhere where) {
        return where(false, column, " < ").bracket(where);
    }

    /**
     * 构建 AND column < (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere NoGreaterBracket(String column, SQLiteWhere where) {
        return where(false, column, " <= ").bracket(where);
    }

    /**
     * 构建 AND column >= (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere NoLessBracket(String column, SQLiteWhere where) {
        return where(false, column, " >= ").bracket(where);
    }

    /**
     * 构建 AND column BETWEEN  (WHERE SQL 语句) AND (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where1 括号内的查询条件值的SQL语句1
     * @param where2 括号内的查询条件值的SQL语句2
     * @return
     */
    public SQLiteWhere BetweenBracket(String column, SQLiteWhere where1, SQLiteWhere where2) {
        return where(false, column, " BETWEEN ").bracket(where1).AndBracket(where2);
    }

    /**
     * 构建 EXISTS (WHERE SQL 语句)查询条件语句
     *
     * @param where 括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere ExistsBracket(SQLiteWhere where) {
        return where(false, "EXISTS").bracket(where);
    }


    /**
     * 构建 OR column LIKE ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值,条件值一般和通配符搭配使用,不匹配大小写:
     *                  % 任意多个字符,包含0个字符;
     *                  _ 任意单个字符;
     * @return
     */
    public SQLiteWhere OrLike(String column, String condition) {
        return where(true, column, " LIKE ?").addArgs(condition);
    }

    /**
     * 构建 OR column = ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere OrEqual(String column, Object condition) {
        return where(true, column, " = ?").addArgs(condition);
    }

    /**
     * 构建 OR column != ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere OrNoEqual(String column, Object condition) {
        return where(true, column, " != ?").addArgs(condition);
    }

    /**
     * 构建 OR column > ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere OrGreater(String column, Object condition) {
        return where(true, column, " > ?").addArgs(condition);
    }

    /**
     * 构建 OR column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere OrLess(String column, Object condition) {
        return where(true, column, " < ?").addArgs(condition);
    }

    /**
     * 构建 OR column <= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere OrNoGreater(String column, Object condition) {
        return where(true, column, " <= ?").addArgs(condition);
    }

    /**
     * 构建 OR column >= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    public SQLiteWhere OrNoLess(String column, Object condition) {
        return where(true, column, " >= ?").addArgs(condition);
    }

    /**
     * 构建 OR BETWEEN  ? AND ? 查询条件语句
     *
     * @param column     查询的行的名称
     * @param condition1 查询条件值1
     * @param condition2 查询条件值2
     * @return
     */
    public SQLiteWhere OrBetween(String column, Object condition1, Object condition2) {
        return where(true, column, " BETWEEN ? AND ?").addArgs(condition1).addArgs(condition2);
    }

    /**
     * 构建 OR column IS NULL 查询条件语句
     *
     * @param column 查询的行的名称
     * @return
     */
    public SQLiteWhere OrIsNull(String column) {
        return where(true, column, " IS NULL");
    }

    /**
     * 构建 OR column IS NOT NULL 查询条件语句
     *
     * @param column 查询的行的名称
     * @return
     */
    public SQLiteWhere OrIsNotNull(String column) {
        return where(true, column, " IS NOT NULL");
    }


    /**
     * 构建 AND column = (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere OrEqualBracket(String column, SQLiteWhere where) {
        return where(true, column, " = ").bracket(where);
    }

    /**
     * 构建 AND column != (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere OrNoEqualBracket(String column, SQLiteWhere where) {
        return where(true, column, " != ").bracket(where);
    }

    /**
     * 构建 AND column > (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere OrGreaterBracket(String column, SQLiteWhere where) {
        return where(true, column, " > ").bracket(where);
    }

    /**
     * 构建 AND column < (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere OrLessBracket(String column, SQLiteWhere where) {
        return where(true, column, " < ").bracket(where);
    }

    /**
     * 构建 AND column < (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere OrNoGreaterBracket(String column, SQLiteWhere where) {
        return where(true, column, " <= ").bracket(where);
    }

    /**
     * 构建 AND column >= (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where  括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere OrNoLessBracket(String column, SQLiteWhere where) {
        return where(true, column, " >= ").bracket(where);
    }

    /**
     * 构建 AND column BETWEEN  (WHERE SQL 语句) AND (WHERE SQL 语句) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param where1 括号内的查询条件值的SQL语句1
     * @param where2 括号内的查询条件值的SQL语句2
     * @return
     */
    public SQLiteWhere OrBetweenBracket(String column, SQLiteWhere where1, SQLiteWhere where2) {
        return where(true, column, " BETWEEN ").bracket(where1).AndBracket(where2);
    }


    /**
     * 构建 OR EXISTS (WHERE SQL 语句)查询条件语句
     *
     * @param where 括号内的查询条件值的SQL语句
     * @return
     */
    public SQLiteWhere OrExistsBracket(SQLiteWhere where) {
        return where(true, "EXISTS ").bracket(where);
    }

    /**
     * 构建 AND column GLOB ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值,条件值一般和通配符搭配使用,匹配大小写:
     *                  * 任意多个字符,包含0个字符
     * @return
     */
    public SQLiteWhere OrGlob(String column, String condition) {
        return where(true, column, " GLOB ?").addArgs(condition);
    }

    /**
     * 构建 AND column GLOB ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值,条件值一般和通配符搭配使用,匹配大小写:
     *                  * 任意多个字符,包含0个字符
     * @return
     */
    public SQLiteWhere OrStartWith(String column, String condition) {
        return where(true, column, " GLOB ?").addArgs(condition + "%");
    }

    /**
     * 构建 AND column GLOB ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值,条件值一般和通配符搭配使用,匹配大小写:
     *                  * 任意多个字符,包含0个字符
     * @return
     */
    public SQLiteWhere OrEndWith(String column, String condition) {
        return where(true, column, " GLOB ?").addArgs("%" + condition);
    }

    /**
     * 构建 AND column GLOB ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值,条件值一般和通配符搭配使用,匹配大小写:
     *                  * 任意多个字符,包含0个字符
     * @return
     */
    public SQLiteWhere OrContain(String column, String condition) {
        return where(true, column, " GLOB ?").addArgs("%" + condition + "%");
    }

}
