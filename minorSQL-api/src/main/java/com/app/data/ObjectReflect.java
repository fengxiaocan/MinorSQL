package com.app.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ObjectReflect {
    private Class aClass;
    private String createTable;
    private String defaultTable;
    private Method createSQL;
    private Method cursorToData;
    private Method cursorToList;

    public ObjectReflect(Class aClass) {
        this.aClass = aClass;
        createTable = (String) getFieldValue("CREATE_TABLE");
        defaultTable = (String) getFieldValue("DEFAULT_TABLE");
        createSQL = getMethod("createSQL", SQLiteDatabase.class);
        cursorToData = getMethod("cursorToData", Cursor.class);
        cursorToList = getMethod("cursorToList", List.class, Cursor.class);
    }

    public String getCreateTable() {
        return createTable;
    }

    public String getDefaultTable() {
        return defaultTable;
    }

    private Object getFieldValue(String name) {
        try {
            return aClass.getField(name).get(null);
        } catch (Exception e) {
            throw new UnRegisterMSQLException();
        }
    }

    public void invokeCreateSQL(SQLiteDatabase database) {
        try {
            createSQL.invoke(null, database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void invokeUpdateSQL(SQLiteDatabase database) {
        try {
            Method updateSQL = getMethod("updateSQL", SQLiteDatabase.class);
            updateSQL.invoke(null, database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object invokeCursorToData(Cursor cursor) {
        try {
            return cursorToData.invoke(null, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void invokeCursorToList(List list, Cursor cursor) {
        try {
            cursorToList.invoke(null, list, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Method getMethod(String name, Class<?>... parameterTypes) {
        try {
            return aClass.getMethod(name, parameterTypes);
        } catch (Exception e) {
            throw new UnRegisterMSQLException();
        }
    }

    public static class UnRegisterMSQLException extends RuntimeException {
        public UnRegisterMSQLException() {
            super("Please use annotations(@SQLite) to mark and register to MSQLHelper.init!");
        }
    }
}
