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

    int update(String tableName);

    int updateOrThrow();

    int updateOrThrow(String tableName);

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
