package com.app.data;

import android.content.ContentValues;

public interface MISQLSupport {
    /************* 增 **************/

    int insert();

    int insertOrThrow();

    int insert(String tableName);

    int insertOrThrow(String tableName);

    /************* 删 **************/

    int delete();

    int delete(String tableName);

    /************* 改 **************/

    int update();

//    int update(long... ids);

    int update(String tableName);

//    int update(String tableName, long... ids);

//    int update(String whereClause, String... conditions);

//    int update(String tableName, String whereClause, String... conditions);

    int updateOrThrow();

//    int updateOrThrow(long... ids);

    int updateOrThrow(String tableName);

//    int updateOrThrow(String tableName, long... ids);

//    int updateOrThrow(String whereClause, String... conditions);

//    int updateOrThrow(String tableName, String whereClause, String... conditions);

    /******************替换或插入*****************/

    int replace();

    int replaceOrThrow();

    int replace(String tableName);

    int replaceOrThrow(String tableName);


    long getSQLiteID();

    /**
     * 获取SQLite的Values
     *
     * @return
     */
    void SQLiteValues(ContentValues value);

    void clearSavedState();

    boolean isSaved();

    String defaultTableName();

//    String uniqueColumn();
}
