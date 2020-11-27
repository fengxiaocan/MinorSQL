package com.app.aptprocessor.processor;

import java.util.Comparator;

public class SQLParams {
    public String name;//名称
    public String type;//类型
    public String sqlType;//数据库类型
    public boolean nullable = true;
    public boolean unique = false;//是否独一无二
    public String defaultValue;//默认值

    public boolean hasDefaultValue() {
        return defaultValue != null && !"".equals(defaultValue);
    }


    public static class SortComparator implements Comparator<SQLParams>{
        @Override
        public int compare(SQLParams sqlParams1, SQLParams sqlParams2) {
            return  sqlParams1.name.compareTo(sqlParams2.name);
        }
    }
}
