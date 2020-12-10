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

    /**
     * 构建 AND WHERE SQl 语句
     *
     * @param whereClause 查询语句,需要自己添加?替代符: column = ?
     * @param condition   查询条件的值,大小不能少于?的数量
     * @return
     */
    @Override
    public QueryHandler whereSql(String whereClause, Object... condition) {
        return (QueryHandler)super.whereSql(whereClause, condition);
    }

    /**
     * 构建 OR WHERE SQl 语句
     *
     * @param whereClause 查询语句,需要自己添加?替代符: column = ?
     * @param condition   查询条件的值,大小不能少于?的数量
     * @return
     */
    @Override
    public QueryHandler orWhereSql(String whereClause, Object... condition) {
        return (QueryHandler)super.orWhereSql(whereClause, condition);
    }

    /**
     * 构建 AND column IN(?,?...?) 查询条件语句
     *
     * @param column     查询的行的名称
     * @param conditions 查询条件值
     * @return
     */
    @Override
    public QueryHandler whereIn(String column, Object... conditions) {
        return (QueryHandler)super.whereIn(column, conditions);
    }

    /**
     * 构建 OR column IN(?,?...?) 查询条件语句
     *
     * @param column     查询的行的名称
     * @param conditions 查询条件值
     * @return
     */
    @Override
    public QueryHandler orWhereIn(String column, Object... conditions) {
        return (QueryHandler)super.orWhereIn(column, conditions);
    }

    /**
     * 构建 AND column IN(?,?...?) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param list   查询条件值的集合
     * @return
     */
    @Override
    public QueryHandler whereIn(String column, List<Object> list) {
        return (QueryHandler)super.whereIn(column, list);
    }

    /**
     * 构建 OR column IN(?,?...?) 查询条件语句
     *
     * @param column 查询的行的名称
     * @param list   查询条件值的集合
     * @return
     */
    @Override
    public QueryHandler orWhereIn(String column, List<Object> list) {
        return (QueryHandler)super.orWhereIn(column, list);
    }

    /**
     * 构建以ID为查询条件语句
     *
     * @param ids 查询的ID值
     * @return
     */
    @Override
    public QueryHandler whereId(long... ids) {
        return (QueryHandler)super.whereId(ids);
    }

    /**
     * 构建 AND column LIKE ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler whereLike(String column, Object condition) {
        return (QueryHandler)super.whereLike(column, condition);
    }

    /**
     * 构建 AND column = ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler whereEqual(String column, Object condition) {
        return (QueryHandler)super.whereEqual(column, condition);
    }

    /**
     * 构建 AND column != ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler whereNoEqual(String column, Object condition) {
        return (QueryHandler)super.whereNoEqual(column, condition);
    }

    /**
     * 构建 AND column > ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler whereGreater(String column, Object condition) {
        return (QueryHandler)super.whereGreater(column, condition);
    }

    /**
     * 构建 AND column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler whereLess(String column, Object condition) {
        return (QueryHandler)super.whereLess(column, condition);
    }

    /**
     * 构建 AND column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler whereNoGreater(String column, Object condition) {
        return (QueryHandler)super.whereNoGreater(column, condition);
    }

    /**
     * 构建 AND column >= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler whereNoLess(String column, Object condition) {
        return (QueryHandler)super.whereNoLess(column, condition);
    }

    /**
     * 构建 OR column LIKE ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler orWhereLike(String column, Object condition) {
        return (QueryHandler)super.orWhereLike(column, condition);
    }

    /**
     * 构建 OR column = ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler orWhereEqual(String column, Object condition) {
        return (QueryHandler)super.orWhereEqual(column, condition);
    }

    /**
     * 构建 OR column != ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler orWhereNoEqual(String column, Object condition) {
        return (QueryHandler)super.orWhereNoEqual(column, condition);
    }

    /**
     * 构建 OR column > ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler orWhereGreater(String column, Object condition) {
        return (QueryHandler)super.orWhereGreater(column, condition);
    }

    /**
     * 构建 OR column < ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler orWhereLess(String column, Object condition) {
        return (QueryHandler)super.orWhereLess(column, condition);
    }

    /**
     * 构建 OR column <= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler orWhereNoGreater(String column, Object condition) {
        return (QueryHandler)super.orWhereNoGreater(column, condition);
    }

    /**
     * 构建 OR column >= ? 查询条件语句
     *
     * @param column    查询的行的名称
     * @param condition 查询条件值
     * @return
     */
    @Override
    public QueryHandler orWhereNoLess(String column, Object condition) {
        return (QueryHandler)super.orWhereNoLess(column, condition);
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
}
