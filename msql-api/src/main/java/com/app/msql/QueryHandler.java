package com.app.msql;

import android.text.TextUtils;

import java.util.List;

public final class QueryHandler extends DataQuery {
    private boolean distinct = false;
    private String[] columns;
    private String groupBy;
    private String having;
    private String orderBy;
    private boolean isDesc;
    private int limit;
    private int offset;

    public QueryHandler(String databaseName, String table) {
        super(databaseName, table);
    }

    /**
     * 选择需要查询的字段,相当于SQL语句的 SELECT columns FROM
     * @param columns 查询的行
     */
    public QueryHandler select(String... columns) {
        this.columns = columns;
        return this;
    }
    /**
     * 查询的是否唯一,相当于SQL语句的 SELECT DISTINCT * FROM
     * @param unique 是否是唯一
     */
    public QueryHandler distinct(boolean unique) {
        this.distinct = unique;
        return this;
    }

    /**
     * 查询的组排序
     * @param groupBy SQL语句的 GROUP BY
     * @param having SQL语句的 HAVING
     */
    public QueryHandler groupBy(String groupBy, String having) {
        this.groupBy = groupBy;
        this.having = having;
        return this;
    }
    /**
     * 查询排序
     * @param orderBy SQL语句的 ORDER BY
     */
    public QueryHandler orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }
    /**
     * 查询排序
     * @param orderBy SQL语句的 ORDER BY
     * @param desc 是否为倒序 SQL语句的 DESC
     */
    public QueryHandler orderBy(String orderBy, boolean desc) {
        this.orderBy = orderBy;
        this.isDesc = desc;
        return this;
    }
    /**
     * 查询多少个数据
     * @param limit SQL语句的 LIMIT
     */
    public QueryHandler limit(int limit) {
        this.limit = limit;
        return this;
    }
    /**
     * 查询数据偏移量
     * @param offset SQL语句的 OFFSET
     */
    public QueryHandler offset(int offset) {
        this.offset = offset;
        return this;
    }

    private String buildQueryString() {
        if (TextUtils.isEmpty(groupBy) && !TextUtils.isEmpty(having)) {
            throw new IllegalArgumentException(
                    "HAVING clauses are only permitted when using a groupBy clause");
        }
        StringBuilder query = new StringBuilder(120);

        query.append("SELECT ");
        if (distinct) {
            query.append("DISTINCT ");
        }
        if (columns != null && columns.length != 0) {
            appendColumns(query, columns);
        } else {
            query.append("* ");
        }
        query.append("FROM ");
        query.append(table);

        if (whereBuilder != null && whereBuilder.length() > 0) {
            query.append(" WHERE ");
            query.append(whereBuilder.toString());
        }
        appendClause(query, " GROUP BY ", groupBy);
        appendClause(query, " HAVING ", having);

        if (!TextUtils.isEmpty(orderBy)) {
            query.append(" ORDER BY ");
            query.append(orderBy);
            if (isDesc) {
                query.append(" DESC");
            }
        }

        if (limit > 0) {
            query.append(" LIMIT ");
            query.append(limit);
            if (offset > 0) {
                query.append(" OFFSET ");
                query.append(offset);
            }
        }
        return query.toString();
    }

    @Override
    protected String conditions() {
        return buildQueryString();
    }


    @Override
    public QueryHandler whereSql(String whereClause, Object... condition) {
        return (QueryHandler)super.whereSql(whereClause, condition);
    }

    @Override
    public QueryHandler orWhereSql(String whereClause, Object... condition) {
        return (QueryHandler)super.orWhereSql(whereClause, condition);
    }

    @Override
    public QueryHandler AndBracket(SQLiteWhere where) {
        return (QueryHandler)super.AndBracket(where);
    }

    @Override
    public QueryHandler OrBracket(SQLiteWhere where) {
        return (QueryHandler)super.OrBracket(where);
    }

    @Override
    public QueryHandler In(String column, Object... conditions) {
        return (QueryHandler)super.In(column, conditions);
    }

    @Override
    public QueryHandler OrIn(String column, Object... conditions) {
        return (QueryHandler)super.OrIn(column, conditions);
    }

    @Override
    public QueryHandler NotIn(String column, Object... conditions) {
        return (QueryHandler)super.NotIn(column, conditions);
    }

    @Override
    public QueryHandler OrNotIn(String column, Object... conditions) {
        return (QueryHandler)super.OrNotIn(column, conditions);
    }

    @Override
    public QueryHandler In(String column, List<Object> list) {
        return (QueryHandler)super.In(column, list);
    }

    @Override
    public QueryHandler OrIn(String column, List<Object> list) {
        return (QueryHandler)super.OrIn(column, list);
    }

    @Override
    public QueryHandler NotIn(String column, List<Object> list) {
        return (QueryHandler)super.NotIn(column, list);
    }

    @Override
    public QueryHandler OrNotIn(String column, List<Object> list) {
        return (QueryHandler)super.OrNotIn(column, list);
    }

    @Override
    public QueryHandler InSqlID(long... ids) {
        return (QueryHandler)super.InSqlID(ids);
    }

    @Override
    public QueryHandler Like(String column, String condition) {
        return (QueryHandler)super.Like(column, condition);
    }

    @Override
    public QueryHandler Glob(String column, String condition) {
        return (QueryHandler)super.Glob(column, condition);
    }

    @Override
    public QueryHandler StartWith(String column, String condition) {
        return (QueryHandler)super.StartWith(column, condition);
    }

    @Override
    public QueryHandler EndWith(String column, String condition) {
        return (QueryHandler)super.EndWith(column, condition);
    }

    @Override
    public QueryHandler Contain(String column, String condition) {
        return (QueryHandler)super.Contain(column, condition);
    }

    @Override
    public QueryHandler Equal(String column, Object condition) {
        return (QueryHandler)super.Equal(column, condition);
    }

    @Override
    public QueryHandler NoEqual(String column, Object condition) {
        return (QueryHandler)super.NoEqual(column, condition);
    }

    @Override
    public QueryHandler Greater(String column, Object condition) {
        return (QueryHandler)super.Greater(column, condition);
    }

    @Override
    public QueryHandler Less(String column, Object condition) {
        return (QueryHandler)super.Less(column, condition);
    }

    @Override
    public QueryHandler NoGreater(String column, Object condition) {
        return (QueryHandler)super.NoGreater(column, condition);
    }

    @Override
    public QueryHandler NoLess(String column, Object condition) {
        return (QueryHandler)super.NoLess(column, condition);
    }

    @Override
    public QueryHandler Between(String column, Object condition1, Object condition2) {
        return (QueryHandler)super.Between(column, condition1, condition2);
    }

    @Override
    public QueryHandler IsNull(String column) {
        return (QueryHandler)super.IsNull(column);
    }

    @Override
    public QueryHandler IsNotNull(String column) {
        return (QueryHandler)super.IsNotNull(column);
    }

    @Override
    public QueryHandler EqualBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.EqualBracket(column, where);
    }

    @Override
    public QueryHandler NoEqualBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.NoEqualBracket(column, where);
    }

    @Override
    public QueryHandler GreaterBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.GreaterBracket(column, where);
    }

    @Override
    public QueryHandler LessBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.LessBracket(column, where);
    }

    @Override
    public QueryHandler NoGreaterBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.NoGreaterBracket(column, where);
    }

    @Override
    public QueryHandler NoLessBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.NoLessBracket(column, where);
    }

    @Override
    public QueryHandler BetweenBracket(String column, SQLiteWhere where1, SQLiteWhere where2) {
        return (QueryHandler)super.BetweenBracket(column, where1, where2);
    }

    @Override
    public QueryHandler ExistsBracket(SQLiteWhere where) {
        return (QueryHandler)super.ExistsBracket(where);
    }

    @Override
    public QueryHandler OrLike(String column, String condition) {
        return (QueryHandler)super.OrLike(column, condition);
    }

    @Override
    public QueryHandler OrEqual(String column, Object condition) {
        return (QueryHandler)super.OrEqual(column, condition);
    }

    @Override
    public QueryHandler OrNoEqual(String column, Object condition) {
        return (QueryHandler)super.OrNoEqual(column, condition);
    }

    @Override
    public QueryHandler OrGreater(String column, Object condition) {
        return (QueryHandler)super.OrGreater(column, condition);
    }

    @Override
    public QueryHandler OrLess(String column, Object condition) {
        return (QueryHandler)super.OrLess(column, condition);
    }

    @Override
    public QueryHandler OrNoGreater(String column, Object condition) {
        return (QueryHandler)super.OrNoGreater(column, condition);
    }

    @Override
    public QueryHandler OrNoLess(String column, Object condition) {
        return (QueryHandler)super.OrNoLess(column, condition);
    }

    @Override
    public QueryHandler OrBetween(String column, Object condition1, Object condition2) {
        return (QueryHandler)super.OrBetween(column, condition1, condition2);
    }

    @Override
    public QueryHandler OrIsNull(String column) {
        return (QueryHandler)super.OrIsNull(column);
    }

    @Override
    public QueryHandler OrIsNotNull(String column) {
        return (QueryHandler)super.OrIsNotNull(column);
    }

    @Override
    public QueryHandler OrEqualBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.OrEqualBracket(column, where);
    }

    @Override
    public QueryHandler OrNoEqualBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.OrNoEqualBracket(column, where);
    }

    @Override
    public QueryHandler OrGreaterBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.OrGreaterBracket(column, where);
    }

    @Override
    public QueryHandler OrLessBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.OrLessBracket(column, where);
    }

    @Override
    public QueryHandler OrNoGreaterBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.OrNoGreaterBracket(column, where);
    }

    @Override
    public QueryHandler OrNoLessBracket(String column, SQLiteWhere where) {
        return (QueryHandler)super.OrNoLessBracket(column, where);
    }

    @Override
    public QueryHandler OrBetweenBracket(String column, SQLiteWhere where1, SQLiteWhere where2) {
        return (QueryHandler)super.OrBetweenBracket(column, where1, where2);
    }

    @Override
    public QueryHandler OrExistsBracket(SQLiteWhere where) {
        return (QueryHandler)super.OrExistsBracket(where);
    }

    @Override
    public QueryHandler OrGlob(String column, String condition) {
        return (QueryHandler)super.OrGlob(column, condition);
    }

    @Override
    public QueryHandler OrStartWith(String column, String condition) {
        return (QueryHandler)super.OrStartWith(column, condition);
    }

    @Override
    public QueryHandler OrEndWith(String column, String condition) {
        return (QueryHandler)super.OrEndWith(column, condition);
    }

    @Override
    public QueryHandler OrContain(String column, String condition) {
        return (QueryHandler)super.OrContain(column, condition);
    }

    @Override
    public QueryHandler NotBetween(String column, Object condition1, Object condition2) {
        return (QueryHandler)super.NotBetween(column, condition1, condition2);
    }

    @Override
    public QueryHandler NotBetweenBracket(String column, SQLiteWhere where1, SQLiteWhere where2) {
        return (QueryHandler)super.NotBetweenBracket(column, where1, where2);
    }

    @Override
    public QueryHandler NotExistsBracket(SQLiteWhere where) {
        return (QueryHandler)super.NotExistsBracket(where);
    }

    @Override
    public QueryHandler OrNotBetween(String column, Object condition1, Object condition2) {
        return (QueryHandler)super.OrNotBetween(column, condition1, condition2);
    }

    @Override
    public QueryHandler OrNotBetweenBracket(String column, SQLiteWhere where1, SQLiteWhere where2) {
        return (QueryHandler)super.OrNotBetweenBracket(column, where1, where2);
    }

    @Override
    public QueryHandler OrNotExistsBracket(SQLiteWhere where) {
        return (QueryHandler) super.OrNotExistsBracket(where);
    }

    @Override
    public QueryHandler Max(String column) {
        return (QueryHandler) super.Max(column);
    }

    @Override
    public QueryHandler Min(String column) {
        return (QueryHandler) super.Min(column);
    }
}
