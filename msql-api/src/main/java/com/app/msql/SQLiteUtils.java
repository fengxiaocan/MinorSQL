package com.app.msql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.data.MISQLSupport;

import java.util.Arrays;
import java.util.List;

public class SQLiteUtils extends SQLBuilder {
    /**
     * 插入到数据库中
     */
    public static <T> int insert(String databaseName, String tableName, List<T> list) {
        try {
            return insertOrThrow(databaseName, tableName, list);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 插入数据到数据库中
     */
    public static <T> int insertOrThrow(String databaseName, String tableName, List<T> list) {
        if (list == null || list.size() == 0) return -1;
        return execInsertSQL(databaseName, tableName, list);
    }

    /**
     * 插入到数据库中
     */
    public static <T> int insert(String databaseName, String tableName, T... array) {
        try {
            return insertOrThrow(databaseName, tableName, array);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 插入数据到数据库中
     */
    public static <T> int insertOrThrow(String databaseName, String tableName, T... array) {
        if (array == null || array.length == 0) return -1;
        return execInsertSQL(databaseName, tableName, Arrays.asList(array));
    }


    /**
     * @param databaseName
     * @param tableName
     * @return
     */
    public static <T> int delete(String databaseName, String tableName, List<T> list) {
        try {
            return deleteOrThrow(databaseName, tableName, list);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @param databaseName
     * @param tableName
     * @return
     */
    public static <T> int delete(String databaseName, String tableName, T... array) {
        try {
            return deleteOrThrow(databaseName, tableName, array);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * 根据数组的id来删除
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    public static <T> int deleteOrThrow(String databaseName, String tableName, List<T> list) {
        if (list == null || list.size() == 0) return -1;
        SQLiteDatabase database = null;
        try {
            database = getDatabase(databaseName);
            final int arrayNum;
            final int lastCount;
            final int listSize = list.size();
            if (listSize > 999) {
                lastCount = listSize % 999;
                arrayNum = listSize / 999 + (lastCount > 0 ? 1 : 0);
            } else {
                arrayNum = 1;
                lastCount = listSize;
            }
            String[] whereArgs = null;
            String SQL = null;
            int arrayIndex = 0;
            int count = 0;

            for (int i = 0; i < arrayNum; i++) {
                final int loopCount;
                if (i == arrayNum - 1) {
                    if (lastCount > 0) {
                        loopCount = lastCount;
                        whereArgs = new String[loopCount];
                        SQL = buildDeleteString(lastCount);
                    } else {
                        loopCount = 999;
                    }
                } else {
                    loopCount = 999;
                }
                //创建替换数组
                if (whereArgs == null) {
                    whereArgs = new String[loopCount];
                }
                if (SQL == null) {
                    SQL = buildDeleteString(loopCount);
                }
                //遍历块数组,开始组合替换参数数组
                for (int i1 = 0; i1 < loopCount; i1++) {
                    MISQLSupport support = (MISQLSupport) list.get(arrayIndex);
                    whereArgs[i1] = String.valueOf(support.getSQLiteID());
                    arrayIndex++;
                }
                //调用SQL语句
                count = database.delete(tableName, SQL, whereArgs);
            }
            return count;
        } finally {
            closeDatabase(database);
        }
    }


    /**
     * 根据数组的id来删除
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    public static <T> int deleteOrThrow(String databaseName, String tableName, T... array) {
        if (array == null || array.length == 0) return -1;
        return deleteOrThrow(databaseName, tableName, Arrays.asList(array));
    }


    /**
     * 根据id为条件来更新
     */
    public static <T> int updateOrThrow(String databaseName, String tableName, List<T> list) {
        if (list == null || list.size() == 0) return -1;
        return execUpdateSQL(databaseName, tableName, null, list);
    }

    /**
     * 根据id为条件来更新
     */
    public static <T> int update(String databaseName, String tableName, List<T> list) {
        try {
            return updateOrThrow(databaseName, tableName, list);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static <T> int updateOrThrow(String databaseName, String tableName,String column, List<T> list) {
        if (list == null || list.size() == 0) return -1;
        return execUpdateSQL(databaseName, tableName, column, list);
    }

    public static <T> int update(String databaseName, String tableName,String column, List<T> list) {
        try {
            return updateOrThrow(databaseName, tableName,column, list);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static <T> int updateOrThrow(String databaseName, String tableName, T... array) {
        if (array == null || array.length == 0) return -1;
        return execUpdateSQL(databaseName, tableName, null, Arrays.asList(array));
    }

    public static <T> int update(String databaseName, String tableName, T... array) {
        try {
            return updateOrThrow(databaseName, tableName, array);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static <T> int updateOrThrow(String databaseName, String tableName,String column, T... array) {
        if (array == null || array.length == 0) return -1;
        return execUpdateSQL(databaseName, tableName, column, Arrays.asList(array));
    }

    public static <T> int update(String databaseName, String tableName,String column, T... array) {
        try {
            return updateOrThrow(databaseName, tableName, column,array);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static <T> int replace(String databaseName, String tableName, List<T> list) {
        try {
            return replaceOrThrow(databaseName, tableName, list);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static <T> int replaceOrThrow(String databaseName, String tableName, List<T> list) {
        if (list == null || list.size() == 0) return -1;
        return execReplaceSQL(databaseName, tableName, list);
    }

    public static <T> int replace(String databaseName, String tableName, T... array) {
        try {
            return replaceOrThrow(databaseName, tableName, array);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static <T> int replaceOrThrow(String databaseName, String tableName, T... array) {
        if (array == null || array.length == 0) return -1;
        return execReplaceSQL(databaseName, tableName, Arrays.asList(array));
    }


    public static int count(String databaseName, String conditions) {
        SQLiteDatabase database = null;
        Cursor query = null;
        try {
            database = getDatabase(databaseName);
            query = database.rawQuery(conditions, null);
            return query.getCount();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            closeCursor(query);
            closeDatabase(database);
        }
    }

    /**
     * 删除数据库表,删除后不会再自动创建,除非自动调用SQL语句来重新创建
     */
    public static void deleteTable(String databaseName, String... tableNames) {
        SQLiteDatabase database = null;
        try {
            database = getDatabase(databaseName);
            for (String tableName : tableNames) {
                database.execSQL("DROP TABLE IF EXISTS '" + tableName + "'");
            }
        } finally {
            closeDatabase(database);
        }
    }

    /**
     * 清空数据库
     */
    public static void clearTable(String databaseName, String... tableNames) {
        SQLiteDatabase database = null;
        try {
            database = getDatabase(databaseName);
            for (String tableName : tableNames) {
                database.execSQL("DELETE FROM '" + tableName + "'");
            }
        } finally {
            closeDatabase(database);
        }
    }

    /**
     * 重新创建数据表
     */
    public static void rebuildTable(String databaseName, String tableName, String createSQL) {
        SQLiteDatabase database = null;
        try {
            database = getDatabase(databaseName);

            database.execSQL("DROP TABLE IF EXISTS '" + tableName + "'");
            database.execSQL(createSQL);
        } finally {
            closeDatabase(database);
        }
    }


    /**
     * 给数据库表重命名
     */
    public static void renameTable(String databaseName, String tableName, String newTableName) {
        SQLiteDatabase database = null;
        try {
            database = getDatabase(databaseName);
            database.execSQL("ALTER TABLE '" + tableName + "' RENAME TO " + newTableName + "'");
        } finally {
            closeDatabase(database);
        }
    }

}
