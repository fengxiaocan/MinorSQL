package com.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.annotation.SQLite;

import java.util.HashSet;
import java.util.Set;

/**
 * 创建同一个类不同数据表的关联工具
 * 原理为插入数据,根据ID来获取对应的类的新的数据表名称,使一个类能对应多个数据表,但是数据结构不变
 */
public class SQLiteRecord {

    private static final String TABLE = "msql_record";
    private static final String TYPE = "clazz";//对应的类的全路径
    private static final String STATEMENT = "create_sql";//创建SQL的语句
    public static final String CREATE_SQL = "CREATE TABLE " + TABLE + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT," + TYPE + " TEXT," + STATEMENT + " TEXT)";
    private static final String TYPE_SELECTION = TYPE + " = ?";

    public static void saveSQL(SQLiteDatabase database, String clazz, String createSQL) {
        ContentValues values = new ContentValues();
        values.put(TYPE, clazz);
        values.put(STATEMENT, createSQL);
        database.replace(TABLE, null, values);
    }

    /**
     * 根据类的名称以及标记来获取到 对应的数据表的名称
     *
     * @param database
     * @param clazz
     * @param createSQL
     * @return 新建的表的名称, 实现为 REL+_ID
     */
    public static boolean checkSQL(SQLiteDatabase database, String clazz, String createSQL) {
        Cursor query = database.query(TABLE, null, TYPE_SELECTION, new String[]{clazz}, null, null, null);
        boolean isCheck = false;
        if (query.moveToLast()) {
            String column = query.getString(query.getColumnIndex(STATEMENT));
            isCheck = column.equals(createSQL);
        }
        if (!isCheck) {
            ContentValues values = new ContentValues();
            values.put(TYPE, clazz);
            values.put(STATEMENT, createSQL);
            database.replace(TABLE, null, values);
        }
        return isCheck;
    }

    /**
     * 更新数据库
     *
     * @param database 数据库
     * @param clazz    类
     * @param sql      sql语句
     */
    public static void updateSQL(SQLiteDatabase database, String clazz, String sql, String[] tableName, Set<String> newColumns) {
        if (!checkSQL(database, clazz, sql)) {
            Cursor query = database.query(tableName[0], null, null, null, null, null, null);
            String[] columnNames = query.getColumnNames();
            query.close();

            Set<String> same = new HashSet<>();
            for (String columnName : columnNames) {
                if (newColumns.contains(columnName)) {
                    same.add(columnName);
                }
            }

            //复制旧表,更换为新的表,复制旧的数据过去
            StringBuilder builder = new StringBuilder();
            for (String keySet : same) {
                builder.append(keySet);
                builder.append(",");

            }
            builder.append(SQLite.SQL_ID);

            String cols = builder.toString();

            for (String newTable : tableName) {
                //升级数据库步骤,Android数据库不支持删除字段,不支持修改字段类型
                String oldTable = "old_" + newTable;
                try {
                    //先改名旧表
                    database.execSQL(join("ALTER TABLE '", newTable, "' RENAME TO ", oldTable));
                    //创建新表
                    database.execSQL(String.format(sql, newTable, newTable));
                    //将旧表的内容插入到新表中
                    String join = join("INSERT INTO '", newTable, "'(", cols, ") SELECT ", cols, " FROM '", oldTable,"'");
                    database.execSQL(join);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //删除旧表
                    database.execSQL("DROP TABLE IF EXISTS '" + oldTable + "'");
                }
            }
        }
    }

    private static String join(Object... objs) {
        if (objs == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Object obj : objs) {
            builder.append(obj);
        }
        return builder.toString();
    }
}
