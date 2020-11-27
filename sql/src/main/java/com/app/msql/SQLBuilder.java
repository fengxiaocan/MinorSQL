package com.app.msql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.app.annotation.SQLite;
import com.app.data.MISQLSupport;

import java.util.Arrays;
import java.util.List;

class SQLBuilder {
    /**
     * 使用拼接的方式批量update数据库，效率提高理论上提高到40-80倍
     * 使用原生逐条更新方式,1000条数据至少要11秒,使用批量更新语句在230毫秒之间
     *
     * @param databaseName 数据库的名称
     * @param tableName    表的名称
     * @param column       需要指定按照哪个行来提高
     * @param list         需要插入的数据库列表
     * @param <T>          必须为MISQLSupport的子类
     * @return
     */
    protected static <T> int execUpdateSQL(String databaseName, String tableName, final String column, List<T> list) {
        ContentValues value = new ContentValues();
        SQLiteDatabase database = null;
        try {
            //获取数据库
            database = getDatabase(databaseName);
            //获取第一个数据
            MISQLSupport support1 = (MISQLSupport) list.get(0);
            support1.SQLiteValues(value);
            //获取保存字段的数量
            final int keySize = value.size();
            if (keySize == 0) {
                return 0;
            }

            //把字段列表变成数组,方便下面转换
            String[] keyArray = new String[keySize];
            keyArray = value.keySet().toArray(keyArray);
            //如果数据库的table为空,获取默认的
            if (TextUtils.isEmpty(tableName)) {
                tableName = support1.defaultTableName();
            }
            final int listSize = list.size();
            //经过测试，一条SQLite语句的 ?,?,? 参数不能超过999个。
            //listSize*2*keySize+listSize<999;
            final int sqlMaxArgsSize = 999 / (keySize * 2 + 1);

            //按照能容纳的最大替代参数,分割输入的数组成块
            Object[] bindArgs = null;
            final int arrayNum;
            final int lastCount;

            if (listSize > sqlMaxArgsSize) {
                lastCount = listSize % sqlMaxArgsSize;
                arrayNum = listSize / sqlMaxArgsSize + (lastCount > 0 ? 1 : 0);
            } else {
                arrayNum = 1;
                lastCount = listSize;
            }

            int arrayIndex = 0;
            //变量数组的块
            String whereSQL = null;

            for (int i = 0; i < arrayNum; i++) {
                final int loopCount;
                if (i == arrayNum - 1) {
                    //最后一块
                    if (lastCount > 0) {
                        loopCount = lastCount;
                        bindArgs = new Object[keySize * loopCount * 2 + loopCount];
                        if (i != 0) {
                            if (column == null) {
                                whereSQL = builderUpdateArgs(tableName, keyArray, SQLite.SQL_ID, loopCount);
                            } else {
                                whereSQL = builderUpdateArgs(tableName, keyArray, column, loopCount);
                            }
                        }
                    } else {
                        //最后一块是第一块
                        loopCount = sqlMaxArgsSize;
                    }
                } else {
                    loopCount = sqlMaxArgsSize;
                }
                //创建替换数组
                if (bindArgs == null) {
                    bindArgs = new Object[keySize * loopCount * 2 + loopCount];
                }
                //第一次需要添加 (?,?...) 替换参数的SQL语句
                if (whereSQL == null) {
                    if (column == null) {
                        whereSQL = builderUpdateArgs(tableName, keyArray, SQLite.SQL_ID, loopCount);
                    } else {
                        whereSQL = builderUpdateArgs(tableName, keyArray, column, loopCount);
                    }
                }
                //遍历块数组,开始组合替换参数数组
                for (int y = 0; y < loopCount; y++) {
                    MISQLSupport support = (MISQLSupport) list.get(arrayIndex);
                    value.clear();
                    support.SQLiteValues(value);
                    //UPDATE tableName SET
                    //cha = CASE _ID WHEN ? THEN ? WHEN ? THEN ? END,
                    //tag = CASE _ID WHEN ? THEN ? WHEN ? THEN ? END,
                    //ver = CASE _ID WHEN ? THEN ? WHEN ? THEN ? END,
                    //WHERE _ID IN (?,?)
                    //WHEN ?(ID) THEN ?(需要更新的数值)
                    for (int k = 0; k < keyArray.length; k++) {
                        int i1 = k * loopCount * 2 + y * 2 + 1;
                        bindArgs[i1] = value.get(keyArray[k]);
                    }
                    if (column == null) {
                        //如果没有设置 行字段 作为条件,使用 id 作为条件
                        long sqLiteID = support.getSQLiteID();
                        bindArgs[bindArgs.length - loopCount + y] = sqLiteID;
                        for (int k = 0; k < keyArray.length; k++) {
                            bindArgs[k * loopCount * 2 + y * 2] = sqLiteID;
                        }
                    } else {
                        Object o = value.get(column);
                        bindArgs[bindArgs.length - loopCount + y] = o;
                        for (int k = 0; k < keyArray.length; k++) {
                            bindArgs[k * loopCount * 2 + y * 2] = o;
                        }
                    }
                    arrayIndex++;
                }
                //调用SQL语句
                synchronized (Object.class) {
                    database.execSQL(whereSQL, bindArgs);
                }
            }
            return listSize;
        } finally {
            closeDatabase(database);
        }
    }

    /**
     * 通过构建插入数据SQL语句批量插入数据,性能提升可达到几十到一百多倍
     *
     * @param databaseName 数据库名称
     * @param tableName    数据表名称
     * @param list         数据列表
     * @param <T>          泛型,必须为MISQLSupport子类
     * @return 返回插入的数据数量
     */
    protected static <T> int execInsertSQL(String databaseName, String tableName, List<T> list) {
        ContentValues value = new ContentValues();
        SQLiteDatabase database = null;
        try {
            //获取数据库
            database = getDatabase(databaseName);
            //INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
            //获取第一个数据
            MISQLSupport support1 = (MISQLSupport) list.get(0);
            support1.SQLiteValues(value);
            //获取保存字段的数量
            final int keySize = value.size();
            if (keySize == 0) {
                return 0;
            }
            //把字段列表变成数组,方便下面转换
            String[] keyArray = new String[keySize];
            keyArray = value.keySet().toArray(keyArray);
            //根据字段数量生成 (?,?,?) 的替代参数,方便下面统一添加
            final String args = builderBindArgs(keySize);
            //如果数据库的table为空,获取默认的
            if (TextUtils.isEmpty(tableName)) {
                tableName = support1.defaultTableName();
            }

            //生成统一的头部
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ");
            sql.append(tableName);
            sql.append('(');

            for (int i = 0; i < keyArray.length; i++) {
                sql.append(keyArray[i]);
                if (i < keyArray.length - 1) {
                    sql.append(",");
                } else {
                    sql.append(") VALUES ");
                }
            }
            final String firstStr = sql.toString();


            //经过测试，一条SQLite语句的 ?,?,? 参数不能超过999个。
            final int sqlMaxArgsSize = 999 / keySize;


            //按照能容纳的最大替代参数,分割输入的数组成块
            Object[] bindArgs = null;
            final int arrayNum;
            final int lastCount;
            final int listSize = list.size();
            if (listSize > sqlMaxArgsSize) {
                lastCount = listSize % sqlMaxArgsSize;
                arrayNum = listSize / sqlMaxArgsSize + (lastCount > 0 ? 1 : 0);
            } else {
                arrayNum = 1;
                lastCount = listSize;
            }

            int arrayIndex = 0;
            //变量数组的块
            for (int i = 0; i < arrayNum; i++) {
                final int loopCount;
                if (i == arrayNum - 1) {
                    //最后一块
                    if (lastCount > 0) {
                        loopCount = lastCount;
                        bindArgs = new Object[loopCount * keySize];
                        if (i != 0) {
                            sql = new StringBuilder(firstStr);
                            addStr(sql, args, loopCount);
                        }
                    } else {
                        //最后一块是第一块
                        loopCount = sqlMaxArgsSize;
                    }
                } else {
                    loopCount = sqlMaxArgsSize;
                }
                //创建替换数组
                if (bindArgs == null) {
                    bindArgs = new Object[loopCount * keySize];
                }
                //第一次需要添加 (?,?...) 替换参数的SQL语句
                if (i == 0) {
                    addStr(sql, args, loopCount);
                }

                int argsIndex = 0;
                //遍历块数组,开始组合替换参数数组
                for (int i1 = 0; i1 < loopCount; i1++) {
                    MISQLSupport support = (MISQLSupport) list.get(arrayIndex);
                    value.clear();
                    support.SQLiteValues(value);
                    for (String key : keyArray) {
                        bindArgs[argsIndex] = value.get(key);
                        argsIndex++;
                    }
                    arrayIndex++;
                }
                //调用SQL语句
                database.execSQL(sql.toString(), bindArgs);
            }
            return listSize;
        } finally {
            closeDatabase(database);
        }
    }


    /**
     * 通过构建 replace 数据SQL语句批量插入数据,性能提升可达到几十到一百多倍
     *
     * INSERT OR REPLACE INTO tableName (_ID, name, tag) VALUES (1, '李宇春', 'CEO') , (2, '李宇春', 'CEO')
     * @param databaseName 数据库名称
     * @param tableName    数据表名称
     * @param list         数据列表
     * @param <T>          泛型,必须为MISQLSupport子类
     * @return 返回插入的数据数量
     */
    protected static <T> int execReplaceSQL(String databaseName, String tableName, List<T> list) {
        ContentValues value = new ContentValues();
        SQLiteDatabase database = null;
        try {
            //获取数据库
            database = getDatabase(databaseName);
            //INSERT OR REPLACE INTO tableName (_ID, name, tag) VALUES (1, '李宇春', 'CEO') , (2, '李宇春', 'CEO')
            //获取第一个数据
            MISQLSupport support1 = (MISQLSupport) list.get(0);
            support1.SQLiteValues(value);
            //获取保存字段的数量
            final int keySize = value.size();
            if (keySize == 0) {
                return 0;
            }
            //把字段列表变成数组,方便下面转换
            String[] keyArray = new String[keySize];
            keyArray = value.keySet().toArray(keyArray);
            //根据字段数量生成 (?,?,?) 的替代参数,方便下面统一添加
            final int columnSize = keySize + 1;
            final String args = builderBindArgs(columnSize);
            //如果数据库的table为空,获取默认的
            if (TextUtils.isEmpty(tableName)) {
                tableName = support1.defaultTableName();
            }

            //生成统一的头部
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT OR REPLACE INTO ");
            sql.append(tableName);
            sql.append(" (_ID,");
            for (int i = 0; i < keyArray.length; i++) {
                sql.append(keyArray[i]);
                if (i < keyArray.length - 1) {
                    sql.append(",");
                } else {
                    sql.append(") VALUES ");
                }
            }
            final String firstStr = sql.toString();

            //经过测试，一条SQLite语句的 ?,?,? 参数不能超过999个。
            final int sqlMaxArgsSize = 999 / (columnSize);

            //按照能容纳的最大替代参数,分割输入的数组成块
            Object[] bindArgs = null;
            final int arrayNum;
            final int lastCount;
            final int listSize = list.size();
            if (listSize > sqlMaxArgsSize) {
                lastCount = listSize % sqlMaxArgsSize;
                arrayNum = listSize / sqlMaxArgsSize + (lastCount > 0 ? 1 : 0);
            } else {
                arrayNum = 1;
                lastCount = listSize;
            }

            int arrayIndex = 0;
            //变量数组的块
            for (int i = 0; i < arrayNum; i++) {
                final int loopCount;
                if (i == arrayNum - 1) {
                    //最后一块
                    if (lastCount > 0) {
                        loopCount = lastCount;
                        bindArgs = new Object[loopCount * (columnSize)];
                        if (i != 0) {
                            sql = new StringBuilder(firstStr);
                            addStr(sql, args, loopCount);
                        }
                    } else {
                        //最后一块是第一块
                        loopCount = sqlMaxArgsSize;
                    }
                } else {
                    loopCount = sqlMaxArgsSize;
                }
                //创建替换数组
                if (bindArgs == null) {
                    bindArgs = new Object[loopCount * (columnSize)];
                }
                //第一次需要添加 (?,?...) 替换参数的SQL语句
                if (i == 0) {
                    addStr(sql, args, loopCount);
                }

                int argsIndex = 0;
                //遍历块数组,开始组合替换参数数组
                for (int i1 = 0; i1 < loopCount; i1++) {
                    MISQLSupport support = (MISQLSupport) list.get(arrayIndex);
                    bindArgs[argsIndex] = support.getSQLiteID();
                    argsIndex++;

                    value.clear();
                    support.SQLiteValues(value);
                    for (String key : keyArray) {
                        bindArgs[argsIndex] = value.get(key);
                        argsIndex++;
                    }
                    arrayIndex++;
                }
                //调用SQL语句
                database.execSQL(sql.toString(), bindArgs);
            }
            return listSize;
        } finally {
            closeDatabase(database);
        }
    }


    private static String builderBindArgs(int size) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i < size; i++) {
            builder.append("?");
            if (i < (size - 1)) {
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * 构建更新的语句,原理:
     * UPDATE tableName SET
     * cha = CASE _ID WHEN ? THEN ? WHEN ? THEN ? END,
     * tag = CASE _ID WHEN ? THEN ? WHEN ? THEN ? END,
     * ver = CASE _ID WHEN ? THEN ? WHEN ? THEN ? END,
     * WHERE _ID IN (?,?)
     * WHEN ?(ID) THEN ?(需要更新的数值)
     *
     * @param tableName 数据库名称
     * @param keySet    数据库插入的行
     * @param column    需要哪个行作为条件
     * @param size      数据大小
     * @return 构建的SQL语句
     */
    private static String builderUpdateArgs(String tableName, String[] keySet, String column, int size) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");
        for (int i = 0; i < keySet.length; i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append(keySet[i]);
            sql.append(" = CASE ");
            sql.append(column);
            sql.append(" ");
            for (int y = 1; y <= size; y++) {
                sql.append("WHEN ? THEN ? ");
            }
            sql.append("END ");
        }
        sql.append("WHERE ");
        sql.append(column);
        sql.append(" IN (");
        for (int i = 0; i < size; i++) {
            if (i < size - 1) {
                sql.append("?,");
            } else {
                sql.append("?");
            }
        }
        sql.append(')');
        return sql.toString();
    }


    protected static void addStr(StringBuilder builder, String add, int size) {
        for (int i = 0; i < size; i++) {
            builder.append(add);
            if (i < size - 1) {
                builder.append(",");
            }
        }
    }

    protected static SQLiteDatabase getDatabase(String databaseName) {
        return MSQLHelper.getSQLiteHelper(databaseName).getWritableDatabase();
    }

    protected static String buildDeleteString(int size) {
        StringBuilder query = new StringBuilder(120);
        query.append(SQLite.SQL_ID);
        query.append(" IN (");
        for (int i = 0; i < size; i++) {
            query.append(i < size - 1 ? "?," : "?");
        }
        query.append(")");
        return query.toString();
    }

    public static void closeCursor(Cursor cursor) {
        try {
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void closeDatabase(SQLiteDatabase database) {
        try {
            if (database != null) database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
