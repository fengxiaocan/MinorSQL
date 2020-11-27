package com.app.aptprocessor.util;

import com.app.aptprocessor.processor.SQLParams;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class SQLUtils {

    public static String getSQLiteType(String type) {
        //NULL、INTEGER、REAL（浮点数字）、TEXT(字符串文本)和BLOB(二进制对象,数据不做任何转换，以输入形式存储。)
        if (JCTreeUtils.isEqualsOf(type, "boolean", "byte", "short", "int", "long", "java.lang.Boolean",
                "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "char", "java.lang.Character")) {
            return "INTEGER";
        } else if (JCTreeUtils.isEqualsOf(type, "float", "double", "java.lang.Float", "java.lang.Double")) {
            return "REAL";
        } else if (JCTreeUtils.isEqualsOf(type, "java.lang.String")) {
            return "TEXT";
        } else {
            return "BLOB";
        }
    }


    public static boolean isBoolean(SQLParams params) {
        return JCTreeUtils.isEqualsOf(params.type, "boolean", "java.lang.Boolean");
    }

    public static boolean isByte(SQLParams params) {
        return JCTreeUtils.isEqualsOf(params.type, "byte", "java.lang.Byte");
    }

    public static boolean isShort(SQLParams params) {
        return JCTreeUtils.isEqualsOf(params.type, "short", "java.lang.Short");
    }

    public static boolean isInt(SQLParams params) {
        return JCTreeUtils.isEqualsOf(params.type, "int", "java.lang.Integer");
    }

    public static boolean isLong(SQLParams params) {
        return JCTreeUtils.isEqualsOf(params.type, "long", "java.lang.Long");
    }

    public static boolean isFloat(SQLParams params) {
        return JCTreeUtils.isEqualsOf(params.type, "float", "java.lang.Float");
    }

    public static boolean isDouble(SQLParams params) {
        return JCTreeUtils.isEqualsOf(params.type, "double", "java.lang.Double");
    }

    public static boolean isChar(SQLParams params) {
        return JCTreeUtils.isEqualsOf(params.type, "char", "java.lang.Character");
    }

    public static boolean isString(SQLParams params) {
        return JCTreeUtils.isEqualsOf(params.type, "java.lang.String");
    }


    public static boolean isBoolean(String type) {
        return JCTreeUtils.isEqualsOf(type, "boolean", "java.lang.Boolean");
    }

    public static boolean isByte(String type) {
        return JCTreeUtils.isEqualsOf(type, "byte", "java.lang.Byte");
    }

    public static boolean isShort(String type) {
        return JCTreeUtils.isEqualsOf(type, "short", "java.lang.Short");
    }

    public static boolean isInt(String type) {
        return JCTreeUtils.isEqualsOf(type, "int", "java.lang.Integer");
    }

    public static boolean isLong(String type) {
        return JCTreeUtils.isEqualsOf(type, "long", "java.lang.Long");
    }

    public static boolean isFloat(String type) {
        return JCTreeUtils.isEqualsOf(type, "float", "java.lang.Float");
    }

    public static boolean isDouble(String type) {
        return JCTreeUtils.isEqualsOf(type, "double", "java.lang.Double");
    }

    public static boolean isChar(String type) {
        return JCTreeUtils.isEqualsOf(type, "char", "java.lang.Character");
    }

    public static boolean isString(String type) {
        return JCTreeUtils.isEqualsOf(type, "java.lang.String");
    }

    public static SQLParams[] getParamsArray(Map<String, SQLParams> paramsMap) {
        Collection<SQLParams> values = paramsMap.values();
        SQLParams[] params = new SQLParams[values.size()];
        params = values.toArray(params);
        Arrays.sort(params, new SQLParams.SortComparator());
        return params;
    }

    public static String getUniqueColumn(Map<String, SQLParams> paramsMap) {
        SQLParams[] paramsArray = getParamsArray(paramsMap);
        for (int i = 0; i < paramsArray.length; i++) {
            SQLParams sqlParams = paramsArray[i];
            if (sqlParams.unique) {
//                list.add(sqlParams);
                return sqlParams.name;
            }
        }
        return null;
    }

    public static StringBuilder getSQLCreateBuilder(Map<String, SQLParams> paramsMap) {
        SQLParams[] paramsArray = getParamsArray(paramsMap);
        StringBuilder builder = new StringBuilder("CREATE TABLE '%s' (_ID INTEGER PRIMARY KEY AUTOINCREMENT,");
//        java.util.List<SQLParams> list = new ArrayList<>();
        SQLParams unique = null;
        for (int i = 0; i < paramsArray.length; i++) {
            SQLParams sqlParams = paramsArray[i];
            builder.append("'");
            builder.append(sqlParams.name);
            builder.append("' ");
            builder.append(sqlParams.sqlType);
            if (!sqlParams.nullable) {
                builder.append(" NOT NULL");
            }
            if (sqlParams.hasDefaultValue()) {
                builder.append(" DEFAULT '");
                builder.append(sqlParams.defaultValue);
                builder.append("'");
            }
            if (sqlParams.unique) {
//                list.add(sqlParams);
                if (unique == null) unique = sqlParams;
            }
            if (i < paramsArray.length - 1) {
                builder.append(",");
            }
        }
//        if (list.size() == 1) {
        //这里只能有一列设置为 unique
        if (unique != null) {
            builder.append(",UNIQUE ('");
//            SQLParams sqlParams = list.get(0);
            builder.append(unique.name);
            builder.append("') ");
        }
//        } else if (list.size() >= 1) {
//            builder.append(",CONSTRAINT uc_%sID UNIQUE (");
//            for (int i = 0; i < list.size(); i++) {
//                SQLParams sqlParams = list.get(i);
//                builder.append("'");
//                builder.append(sqlParams.name);
//                builder.append("'");
//                if (i < list.size() - 1) {
//                    builder.append(",");
//                }
//            }
//            builder.append(")");
//        }
        builder.append(")");
        return builder;
    }
}
