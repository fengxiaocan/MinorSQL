package com.app.aptprocessor.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.lang.model.element.Modifier;

public class SQLInterfaceJavaCode {
    private java.util.List<SQLTable> tableNames;//tableName
    private Map<String, SQLParams> paramsMap;
    private String SQLiteID;
    private String mClassName;
    private String mPackageName;
    private ClassName stringClassName = ClassName.get(String.class);

    public SQLInterfaceJavaCode(java.util.List<SQLTable> tableNames, Map<String, SQLParams> paramsMap, String SQLiteID, Symbol.ClassSymbol classSymbol) {
        this.tableNames = tableNames;
        this.paramsMap = paramsMap;
        this.SQLiteID = SQLiteID;
        this.mPackageName = classSymbol.packge().getQualifiedName().toString();
        this.mClassName = classSymbol.getSimpleName().toString() + "SQL";
    }

    /**
     * 创建Java代码
     * javapoet
     *
     * @return type spec
     */
    public TypeSpec generateJavaCode() {
        return TypeSpec.interfaceBuilder(mClassName)
                .addModifiers(Modifier.PUBLIC)
                .addFields(fieldSpecs())
                .build();
    }

    public String getPackageName() {
        return mPackageName;
    }

    /**
     * 生成成员变量
     *
     * @return
     */
    private FieldSpec generateConstantFields(String name, String format, Object... args) {
        return FieldSpec.builder(stringClassName, name, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(format, args).build();
    }

    private Iterable<FieldSpec> fieldSpecs() {
        HashSet<FieldSpec> set = new LinkedHashSet<>();
        for (int i = 0; i < tableNames.size(); i++) {
            SQLTable sqlTable = tableNames.get(i);
            String name;
            if (i == 0) {
                name = "DEFAULT";
            } else {
                name = sqlTable.name.toUpperCase();
            }
            FieldSpec fieldSpec = generateConstantFields(name + "_TABLE", "\"" + sqlTable.table + "\"");
            set.add(fieldSpec);
        }

        if (SQLiteID != null) {
            FieldSpec fieldSpec = generateConstantFields(SQLiteID, "com.app.annotation.SQLite.SQL_ID");
            set.add(fieldSpec);
        }
        if (paramsMap != null) {
            for (String keySet : paramsMap.keySet()) {
                SQLParams params = paramsMap.get(keySet);
                FieldSpec fieldSpec = generateConstantFields(keySet, "\"" + params.name + "\"");
                set.add(fieldSpec);
            }
        }
        return set;
    }
}
