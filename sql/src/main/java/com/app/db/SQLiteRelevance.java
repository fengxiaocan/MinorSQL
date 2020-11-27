package com.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建同一个类不同数据表的关联工具
 * 原理为插入数据,根据ID来获取对应的类的新的数据表名称,使一个类能对应多个数据表,但是数据结构不变
 */
public class SQLiteRelevance  {

    private static final String NAME_PREFIX = "rel";
    private static final String TABLE = "msql_relevance";
    private static final String TYPE = "type";//对应的类的全路径
    private static final String TAG = "tag";//独有唯一的标记,实现为 type+用户输入的标记 的MD5值
    private static final String TYPE_SELECTION = TYPE+" = ?";
    private static final String TAG_SELECTION = TAG + " = ?";

    public static final String CREATE_SQL = "CREATE TABLE " + TABLE + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT,"+TYPE+" TEXT,"+TAG+" TEXT)";

    /**
     * 根据类的名称以及标记来获取到 对应的数据表的名称
     * @param database
     * @param clazzName
     * @param label
     * @return 新建的表的名称,实现为 REL+_ID
     */
    public static String findTableByTag(SQLiteDatabase database,String CREATE_SQL, String clazzName, String label){
        final String codeTag = codeTag(clazzName, label);
        String relevance = findByTag(database, codeTag);
        if (relevance == null){
            ContentValues values = new ContentValues();
            values.put(TYPE,clazzName);
            values.put(TAG, codeTag);
            long id = database.insert(TABLE, null, values);
            String tableName = getTableName(id);
            try {
                database.execSQL(String.format(CREATE_SQL, tableName, tableName));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tableName;
        }
        return relevance;
    }

    /**
     * 根据类的名称以及标记来获取到 对应的数据表的名称
     * @param database
     * @param clazz
     * @param label
     * @return 新建的表的名称,实现为 REL+_ID
     */
    public static String findTableByTag(SQLiteDatabase database,String CREATE_SQL,  Class clazz, String label){
        return findTableByTag(database, CREATE_SQL,clazz.getName(),label);
    }

    /**
     * 查找对应类的所有注册表名
     */
    public static List<String> findTablesByType(SQLiteDatabase database, String clazzName) {
        Cursor query = database.query(TABLE, null,TYPE_SELECTION , new String[]{clazzName}, null, null, null);
        return findTableNames(query, new ArrayList<>());
    }


    /**
     * 查找对应类的所有注册表名
     */
    public static List<String> findTablesByType(SQLiteDatabase database, Class clazz) {
        return findTablesByType(database, clazz.getName());
    }


    /**
     * 根据ID来 获取对应的 表的名称
     */
    private static String getTableName(long id){
        return NAME_PREFIX + id;
    }

    /**
     * 把用户标签以及class类转化为独一无二的标记
     */
    private static String codeTag(String type, String inputLabel) {
        String content = type + inputLabel;
        try {
            char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(content.getBytes());
            StringBuilder hexValue = new StringBuilder(md5Bytes.length * 2);
            for (byte b : md5Bytes) {
                hexValue.append(hexDigits[(b >> 4) & 0x0f]);
                hexValue.append(hexDigits[b & 0x0f]);
            }
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return content;
        }
    }

    /**
     * 查找出对应 TAG 的数据表名称
     * @param database
     * @param tag
     * @return
     */
    private static String findByTag(SQLiteDatabase database, String tag) {
        Cursor query = database.query(TABLE, null, TAG_SELECTION, new String[]{tag}, null, null, null);
        if (query.moveToLast()) {
            long id = query.getLong(0);
            return getTableName(id);
        }
        return null;
    }

    /**
     * 查询相关的数据表名称的集合
     * @param query
     * @param list
     * @return
     */
    private static List<String> findTableNames(Cursor query, List<String> list) {
        while (query.moveToNext()) {
            long id = query.getLong(0);
            list.add(getTableName(id));
        }
        return list;
    }
}
