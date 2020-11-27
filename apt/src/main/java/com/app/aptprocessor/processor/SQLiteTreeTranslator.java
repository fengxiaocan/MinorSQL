package com.app.aptprocessor.processor;

import com.app.annotation.Column;
import com.app.annotation.SQLite;
import com.app.aptprocessor.base.BaseTreeTranslator;
import com.app.aptprocessor.base.JCTreeHelper;
import com.app.aptprocessor.util.JCTreeUtils;
import com.app.aptprocessor.util.SQLUtils;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Pattern;


import static com.app.aptprocessor.processor.SQLiteProcessor.TABLE_NAMES;

public class SQLiteTreeTranslator extends BaseTreeTranslator {
    //NULL、INTEGER、REAL（浮点数字）、TEXT(字符串文本)和BLOB(二进制对象)
    public static final String SQLiteName = SQLite.class.getName();
    public static final String SQL_ID = SQLite.SQL_ID;
    public static final String ColumnName = Column.class.getName();
    public static final String SQL_UTILS = "com.app.msql.SQLiteUtils";
    public static final String SQL_DATABASE = "android.database.sqlite.SQLiteDatabase";
    public static final String CURSOR = "android.database.Cursor";
    public static final String SQL_RELEVANCE = "com.app.db.SQLiteRelevance";
    public static final String SQL_RECORD = "com.app.db.SQLiteRecord.updateSQL";
    public static final String SQL_SUPPORT = "com.app.data.SQLSupport";
    public static final String MI_SQL_SUPPORT = "com.app.data.MISQLSupport";
    public static final String CONTENT_VALUES = "android.content.ContentValues";

    public final String SQL_CLASS;
    public final String SQL_DEFAULT_TABLE;
    private java.util.List<SQLTable> tableNames = new ArrayList<>();//保存
    private Map<String, SQLParams> paramsMap = new LinkedHashMap<>();
    private String SQLiteID;
    private boolean idIsLong = true;
    private JCTree.JCExpression DEFAULT_TABLE_FIELD;

    public SQLiteTreeTranslator(Symbol.ClassSymbol symbol, JCTreeHelper helper) {
        super(symbol, helper);
        SQL_CLASS = fullClassName + "SQL";
        SQL_DEFAULT_TABLE = fullClassName + "SQL.DEFAULT_TABLE";
        DEFAULT_TABLE_FIELD = jcHelper.Access(SQL_DEFAULT_TABLE);
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
        super.visitClassDef(jcClassDecl);
        //这个会是最后才执行的
        if (jcClassDecl.name.toString().equals(simpleClassName)) {
            LinkedHashSet<JCTree> hashSet = new LinkedHashSet<>();
            for (JCTree def : jcClassDecl.defs) {
                hashSet.add(def);
            }
            try {
                //创建SQL路由接口类
                java.util.List<JCTree> list = new java.util.ArrayList<>();
                list.add(createDefaultTable());
                list.add(createTable());
                list.add(createTableMethod(jcClassDecl));
                list.add(createSQL(jcClassDecl));
                list.add(updateSQL(jcClassDecl));
                list.add(cursorToData(jcClassDecl));
                list.add(cursorToList(jcClassDecl));
                List<JCTree> treeList = List.from(list);
                hashSet.add(jcHelper.createInterface("SQL", treeList));
                //创建MISQLSupport的方法
                if (SQLiteID == null) {
                    //添加private long _ID;用于记录数据库的ID,如果已经声明过@SQLiteID,则不会自动生成这个ID
                    hashSet.add(createSQLId());
                }
                hashSet.add(getSQLiteID());
                hashSet.add(setSQLiteID(jcClassDecl));
                hashSet.add(isSaved());
                hashSet.add(clearSavedState());
                hashSet.add(SQLiteValues(jcClassDecl));
                hashSet.add(insert(jcClassDecl));
                hashSet.add(insert());
                hashSet.add(insertOrThrow(jcClassDecl));
                hashSet.add(insertOrThrow());
                hashSet.add(delete(jcClassDecl));
                hashSet.add(delete());
                hashSet.add(replace());
                hashSet.add(replaceOrThrow());
                hashSet.add(replace(jcClassDecl));
                hashSet.add(replaceOrThrow(jcClassDecl));

                //hashSet.add(update(jcClassDecl));
                //hashSet.add(update2(jcClassDecl));
                //hashSet.add(update3(jcClassDecl));
                //hashSet.add(update4(jcClassDecl));
                hashSet.add(update5(jcClassDecl));
                hashSet.add(update());

                //hashSet.add(updateOrThrow(jcClassDecl));
                //hashSet.add(updateOrThrow2(jcClassDecl));
                //hashSet.add(updateOrThrow3(jcClassDecl));
                //hashSet.add(updateOrThrow4(jcClassDecl));
                hashSet.add(updateOrThrow5(jcClassDecl));
                hashSet.add(updateOrThrow());

                hashSet.add(defaultTableName());
//                hashSet.add(uniqueColumn());

                jcClassDecl.defs = List.from(hashSet);

                if (jcClassDecl.extending == null) {
                    jcClassDecl.extending = jcHelper.Access(SQL_SUPPORT);
                } else {
                    if (!MI_SQL_SUPPORT.equals(jcClassDecl.extending.type.toString())) {
                        //添加接口
                        ListBuffer<JCTree.JCExpression> buffer = new ListBuffer<>();
                        for (JCTree.JCExpression jcExpression : jcClassDecl.implementing) {
                            buffer.append(jcExpression);
                        }
                        buffer.append(jcHelper.Access(MI_SQL_SUPPORT));
                        jcClassDecl.implementing = buffer.toList();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                printMessage(e);
            }
        }
    }

    @Override
    public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
        super.visitVarDef(jcVariableDecl);
        //vartype为数据的类型,init为初始化的值,name为该变量的声明名称,mods为修饰方法
        //vartype类型:: 数组类型为JCTree.JCArrayTypeTree  Map/List/Set等集合类型为JCTree.JCTypeApply
        // 基本数据类型为JCTree.JCPrimitiveTypeTree 其余Object类型(包含)为JCTree.JCIdent

        //先判断类型,再判断是否为当前类的成员变量,再判断是否忽略
        //1.先判断是否为当前类的成员变量
        if (JCTreeUtils.isOwnVariable(thisElement, jcVariableDecl)) {
            //2.再判断类型是否为符合
            if (JCTreeUtils.checkModifier(jcVariableDecl)) {
                //----->>>>>判断是否为8大数据类型Or字符串类型
                if (JCTreeUtils.isBasicType(jcVariableDecl)) {
                    //----->>>>>判断是否有注解
                    if (JCTreeUtils.isHasAnnotation(jcVariableDecl)) {
                        //----->>>>>有注解先判断是否需要忽略
                        for (JCTree.JCAnnotation annotation : jcVariableDecl.mods.annotations) {
                            if (ColumnName.equals(annotation.type.toString())) {
                                SQLParams params = disposeParamsAnnotation(jcVariableDecl, annotation);
                                if (params != null) {
                                    if (params.name == null) {
                                        params.name = checkParamsName(jcVariableDecl.name.toString());
                                    }
                                    addToParams(jcVariableDecl, params);
                                }
                            }
                        }
                    } else {
                        //----->>>>>没有注解,添加到参数变量中
                        SQLParams params = new SQLParams();
                        params.name = checkParamsName(jcVariableDecl.name.toString());
                        addToParams(jcVariableDecl, params);
                    }
                }
            }
        }
    }

    public SQLInterfaceJavaCode getJavaCode() {
        return new SQLInterfaceJavaCode(tableNames, paramsMap, SQLiteID, thisElement);
    }

    /**
     * 分析注解
     *
     * @param jcAnnotation
     */
    @Override
    public void visitAnnotation(JCTree.JCAnnotation jcAnnotation) {
        super.visitAnnotation(jcAnnotation);
        //在扫描到 Annotation 注解之后才会执行这一步,多个注解不会在同一方法内执行
        if (SQLiteName.equals(jcAnnotation.type.toString())) {
            //获取到SQLite的table名称
            if (jcAnnotation.args != null && jcAnnotation.args.length() > 0) {
                for (JCTree.JCExpression expression : jcAnnotation.args) {
                    if (expression instanceof JCTree.JCAssign) {
                        JCTree.JCAssign jcAssign = (JCTree.JCAssign) expression;
                        if (jcAssign.lhs instanceof JCTree.JCIdent) {
                            //注解的赋值的名的类型 一般为 JCTree.JCIdent
                            JCTree.JCIdent lhs = (JCTree.JCIdent) jcAssign.lhs;
                            if ("table".equals(lhs.name.toString())) {
                                if (jcAssign.rhs instanceof JCTree.JCNewArray) {
                                    addTableName(simpleClassName);

                                    //如果注解的赋值的值的类型为数组,即是[],类型为 JCTree.JCNewArray
                                    JCTree.JCNewArray rhs = (JCTree.JCNewArray) jcAssign.rhs;
                                    //数组内的值保存在成员变量elems中 rhs.elems
                                    List<JCTree.JCExpression> elems = rhs.elems;
                                    if (elems != null && elems.size() > 0) {
                                        for (JCTree.JCExpression elem : elems) {
                                            if (elem instanceof JCTree.JCLiteral) {
                                                JCTree.JCLiteral literal = (JCTree.JCLiteral) elem;
                                                String value = (String) literal.value;
                                                addTableName(value);
                                            } else if (elem instanceof JCTree.JCFieldAccess) {
                                                JCTree.JCFieldAccess jcFieldAccess = (JCTree.JCFieldAccess) elem;
                                                //printMessage("visitAnnotation:%s",jcFieldAccess.name);//
                                                //printMessage("visitAnnotation:%s",jcFieldAccess.type);
                                                //printMessage("visitAnnotation:%s",jcFieldAccess.selected);
                                                //name为成员变量名称,type为类型,selected为类,静态常量放在 sym 中的 getConstantValue 中
                                                if (jcFieldAccess.sym instanceof Symbol.VarSymbol) {
                                                    Symbol.VarSymbol sym = (Symbol.VarSymbol) jcFieldAccess.sym;
                                                    //获取该类声明的静态常量拥有的值 getConstantValue()
                                                    Object constantValue = sym.getConstantValue();
                                                    addTableName(((String) constantValue));
                                                }
                                            }
                                        }
                                    }
                                } else if (jcAssign.rhs instanceof JCTree.JCLiteral) {
                                    //如果注解的赋值的值的类型为字符串等对象类型,类型为 JCTree.JCLiteral
                                    JCTree.JCLiteral rhs = (JCTree.JCLiteral) jcAssign.rhs;
                                    //数据类型的值保存在成员变量value中 rhs.value
                                    String value = (String) rhs.value;
                                    addTableName(value);
                                }
                            }
                        }
                    }
                }
                if (tableNames.size() == 0) {
                    addTableName(simpleClassName);
                }
            } else {
                addTableName(simpleClassName);
            }
        }
    }

    /**
     * 检测参数名字是否符合标准
     *
     * @param name
     * @return
     */
    private String checkParamsName(String name) {
        if (!Pattern.matches("^[_a-zA-Z]\\w*$", name)) {
            throwException("%s >>> 数据库列表的字段以字母或_开头,不能使用汉字和字符('_'除外): %s", fullClassName, name);
        }
        return name.toLowerCase();
    }

    /**
     * 添加到参数中
     *
     * @param dec
     * @param params
     */
    private void addToParams(JCTree.JCVariableDecl dec, SQLParams params) {
        params.type = JCTreeUtils.getVariableType(dec);
        params.sqlType = SQLUtils.getSQLiteType(params.type);
        printMessage("添加数据列声明命名:%s   类型:%s", params.name, params.type);
        paramsMap.put(dec.name.toString(), params);
    }

    /**
     * 添加为数据库的表名
     *
     * @param tableName
     * @return
     */
    private void addTableName(String tableName) {
        if (tableName == null || "".equals(tableName)) {
            throwException("%s >>> 设置数据库的名称不能为空", fullClassName);
        }
        if (!Pattern.matches("^[_a-zA-Z]\\w*$", tableName)) {
            throwException("%s >>> 设置数据库的名称以字母或_开头,不能使用汉字和字符('_'除外):", fullClassName, tableName);
        }
        tableName = tableName.toLowerCase();

        if (TABLE_NAMES.contains(tableName)) {
            int index = 1;
            String table = tableName + index;
            while (TABLE_NAMES.contains(table)) {
                index++;
                table = tableName + index;
            }
            TABLE_NAMES.add(table);
            tableNames.add(new SQLTable(tableName, table));
            printMessage("%s 生成数据表: %s", fullClassName, table);
        } else {
            TABLE_NAMES.add(tableName);
            tableNames.add(new SQLTable(tableName, tableName));
            printMessage("%s 生成数据表: %s", fullClassName, tableName);
        }
    }

    /**
     * 处理注解
     *
     * @param jcAnnotation
     */
    private SQLParams disposeParamsAnnotation(JCTree.JCVariableDecl dec, JCTree.JCAnnotation jcAnnotation) {
        SQLParams params = new SQLParams();
        if (jcAnnotation.args != null && jcAnnotation.args.size() > 0) {
            //注解内的值保存在 jcAnnotation.args中
            for (JCTree.JCExpression expression : jcAnnotation.args) {
                if (expression instanceof JCTree.JCAssign) {
                    //注解的赋值类型 为 JCTree.JCAssign
                    JCTree.JCAssign jcAssign = (JCTree.JCAssign) expression;
                    if (jcAssign.lhs instanceof JCTree.JCIdent) {
                        //注解的赋值的名的类型 一般为 JCTree.JCIdent
                        JCTree.JCIdent lhs = (JCTree.JCIdent) jcAssign.lhs;
                        //printMessage("disposeParamsAnnotation:Variable=%s lhs.name=%s", dec.name, lhs.name);
                        //printMessage("disposeParamsAnnotation: jcAssign.rhs.class=%s", jcAssign.rhs.getClass());
                        /*if (jcAssign.rhs instanceof JCTree.JCNewArray) {
                            //如果注解的赋值的值的类型为数组,即是[],类型为 JCTree.JCNewArray
                            JCTree.JCNewArray rhs = (JCTree.JCNewArray) jcAssign.rhs;
                            //数组内的值保存在成员变量elems中
                        } */
                        String name = lhs.name.toString();
                        if (jcAssign.rhs instanceof JCTree.JCLiteral) {
                            //如果注解的赋值的值的类型为string等对象类型,类型为 JCTree.JCLiteral
                            JCTree.JCLiteral rhs = (JCTree.JCLiteral) jcAssign.rhs;
                            //数据类型的值保存在成员变量value中
                            if (checkValue(dec, params, name, rhs.value)) return null;
                            //printMessage("rhs.name : %s   rhs.value : %s",name,rhs.value);
                        } else if (jcAssign.rhs instanceof JCTree.JCIdent) {
                            //如果注解的赋值的值的类型为自身类中的常量,类型为 JCTree.JCIdent
                            JCTree.JCIdent rhs = (JCTree.JCIdent) jcAssign.rhs;
                            if (rhs.sym instanceof Symbol.VarSymbol) {
                                Symbol.VarSymbol sym = (Symbol.VarSymbol) rhs.sym;
                                //printMessage("rhs.sym.value : %s", value);
                                if (checkValue(dec, params, name, sym.getConstantValue()))
                                    return null;
                            }
                        } else if (jcAssign.rhs instanceof JCTree.JCFieldAccess) {
                            //如果注解的赋值的值的类型为别的类的常量,类型为 JCTree.JCFieldAccess
                            JCTree.JCFieldAccess rhs = (JCTree.JCFieldAccess) jcAssign.rhs;
                            if (rhs.sym instanceof Symbol.VarSymbol) {
                                Symbol.VarSymbol sym = (Symbol.VarSymbol) rhs.sym;
                                //printMessage("sym : %s ", sym.getConstValue());
                                if (checkValue(dec, params, name, sym.getConstantValue()))
                                    return null;
                            }
                        }
                    }
                }
            }
            return params;
        } else {
            return params;
        }
    }

    private boolean checkValue(JCTree.JCVariableDecl dec, SQLParams params, String name, Object value) {
        if (name.equals("ignore")) {
            //是否忽略
            if ((int) value == 1) {
                return true;
            }
        } else if (name.equals("unique")) {
            //是否唯一
            params.unique = (int) value == 1;
        } else if (name.equals("id")) {
            //是否设置为ID
            String type = dec.vartype.type.toString();
            if ((int) value == 1 && SQLiteID == null &&
                    (SQLUtils.isInt(type) || SQLUtils.isLong(type))) {
                //判断类型是否为long 或者 int
                SQLiteID = dec.name.toString();
                idIsLong = SQLUtils.isLong(type);
                return true;
            }
        } else if (name.equals("nullable")) {
            //是否可以为null
            params.nullable = (int) value == 1;
        } else if (name.equals("name")) {
            //名字
            params.name = checkParamsName((String) value);
        } else if (name.equals("defaultValue")) {
            //默认值
            params.defaultValue = (String) value;
        }
        return false;
    }

    /***************** 创建SQL中的方法 *****************/
    /*获取tableName*/
    private String tableName(int index) {
        String conName;
        if (index == 0) {
            conName = SQL_DEFAULT_TABLE;
        } else {
            SQLTable sqlTable = tableNames.get(index);
            conName = JCTreeUtils.join(SQL_CLASS, ".", sqlTable.name.toUpperCase(), "_TABLE");
        }
        return conName;
    }

    /**
     * 生成创建数据库的SQL语句
     *
     * @return
     */
    private JCTree.JCVariableDecl createTable() {
        StringBuilder builder = SQLUtils.getSQLCreateBuilder(paramsMap);
        int modifiers = Flags.PUBLIC | Flags.STATIC | Flags.FINAL;
        return jcHelper.MemberVariable(modifiers, "CREATE_TABLE", String.class, builder.toString());
    }

    /**
     * 生成创建数据库的SQL语句
     *
     * @return
     */
    private JCTree.JCVariableDecl createDefaultTable() {
        int modifiers = Flags.PUBLIC | Flags.STATIC | Flags.FINAL;
        return jcHelper.MemberVariable(modifiers, "DEFAULT_TABLE", "String",DEFAULT_TABLE_FIELD);
    }

    /**
     * 获取tableName的方法体参数
     */
    private List<JCTree.JCVariableDecl> getNameParameter(JCTree.JCClassDecl jcClassDecl) {
        // 生成方法参数
        JCTree.JCVariableDecl parameter = jcHelper.MethodParams("name", "String", jcClassDecl);
        //包装方法参数
        return List.of(parameter);
    }

    /**
     * createTable(String name) 的方法
     */
    private JCTree.JCMethodDecl createTableMethod(JCTree.JCClassDecl jcClassDecl) {
        //返回值的类型
        JCTree.JCExpression returnType = jcHelper.Ident("String");
        // 生成方法参数 name
        List<JCTree.JCVariableDecl> parameters = getNameParameter(jcClassDecl);
        //获取当前类的成员变量
        JCTree.JCExpression create_table = jcHelper.Ident("CREATE_TABLE");
        JCTree.JCExpression tableName = jcHelper.Ident("name");
        //调用String的format方法
        JCTree.JCExpressionStatement method = jcHelper.execMethod("String.format", create_table, tableName, tableName);
        //作为返回值返回,再构成语句
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makeStaticMethod("createTable", returnType, parameters, block);
    }


    /**
     * createSQL(SQLiteDatabase database) 的方法
     */
    private JCTree.JCMethodDecl createSQL(JCTree.JCClassDecl dec) {
        //创建 SQLiteDatabase db的形参
        JCTree.JCVariableDecl param = jcHelper.Params("db", SQL_DATABASE, dec);
        ListBuffer<JCTree.JCStatement> buffer = jcHelper.buffer();
        for (int i = 0; i < tableNames.size(); i++) {
            //创建tableName的标识符声明,调用其他类的常量必须使用memberAccess而不能使用makeType
            JCTree.JCExpression table = jcHelper.Access(tableName(i));
            //调用createTable(tableName)方法
            JCTree.JCMethodInvocation createTable = jcHelper.callMethod("createTable", table);
            //调用并执行 db.execSQL(SQL) 的方法
            JCTree.JCExpressionStatement execMethod = jcHelper.execMethod("db", "execSQL", createTable);
            buffer.append(execMethod);
        }

        JCTree.JCBlock jcBlock = jcHelper.Block(buffer);
        return jcHelper.makeStaticVoidMethod("createSQL", List.of(param), jcBlock);
    }

    /**
     * createSQL(SQLiteDatabase database) 的方法
     */
    private JCTree.JCMethodDecl updateSQL(JCTree.JCClassDecl dec) {
        //创建 SQLiteDatabase db的形参
        JCTree.JCVariableDecl param = jcHelper.Params("database", SQL_DATABASE, dec);
        ListBuffer<JCTree.JCStatement> buffer = jcHelper.buffer();

        //java.util.List<String> tables
        JCTree.JCTypeApply tablesList = jcHelper.ListCollection("String");
        //SQLiteRelevance.findTablesByType(database, Demo.class.getName());
        JCTree.JCLiteral value = jcHelper.Value(fullClassName);
        JCTree.JCIdent database = jcHelper.Ident("database");
        JCTree.JCMethodInvocation tablesByType = jcHelper.callMethod(SQL_RELEVANCE, "findTablesByType", database, value);
        //java.util.List<String> tables = SQLiteRelevance.findTablesByType(database, Demo.class.getName());
        JCTree.JCVariableDecl decl = jcHelper.BlockVariable("tables", tablesList, tablesByType);
        buffer.append(decl);

        for (int i = tableNames.size() - 1; i >= 0; i--) {
            JCTree.JCExpression table = jcHelper.Access(tableName(i));
            //tables.add(0, DemoSQL.DETAIL_TABLE);
            JCTree.JCExpressionStatement add = jcHelper.execMethod("tables.add", jcHelper.Value(0), table);
            buffer.append(add);
        }

        //String[] tableArray = new String[tables.size()];
        JCTree.JCArrayTypeTree array = jcHelper.StringArray();
        JCTree.JCMethodInvocation method1 = jcHelper.callMethod("tables.size");
        JCTree.JCNewArray initArray = jcHelper.newStringArrayInitSize(method1);
        JCTree.JCVariableDecl tableArray = jcHelper.BlockVariable("tableArray", array, initArray);
        buffer.append(tableArray);
        //tableArray = tables.toArray(tableArray);
        JCTree.JCIdent arrayIdent = jcHelper.Ident("tableArray");
        JCTree.JCMethodInvocation toArray = jcHelper.callMethod("tables.toArray", arrayIdent);
        buffer.append(jcHelper.ExecAssign(arrayIdent, toArray));
        //Set<String> tableNames = new HashSet<>();

        JCTree.JCTypeApply tableExpr = jcHelper.SetCollection("String");
        JCTree.JCExpression hashSet = jcHelper.Access("java.util.HashSet");
        JCTree.JCNewClass NewHashSet = jcHelper.NewClass(jcHelper.Collection(hashSet, jcHelper.StringIdent()));
        buffer.append(jcHelper.BlockVariable("tableNames", tableExpr, NewHashSet));

        //tableNames.add(DemoSQL.name);
        for (String keySet : paramsMap.keySet()) {
            String consName = SQL_CLASS + "." + keySet;
            buffer.append(jcHelper.execMethod("tableNames.add", jcHelper.Access(consName)));
        }

        //SQLiteRecord.updateSQL(database,"com.app.msql.Demo", CREATE_TABLE, tableArray, tableNames);
        JCTree.JCLiteral className = jcHelper.Value(fullClassName);
        JCTree.JCExpression create_table = jcHelper.Ident("CREATE_TABLE");
        JCTree.JCExpression tableNames = jcHelper.Ident("tableNames");
        buffer.append(jcHelper.execMethod(SQL_RECORD, database, className, create_table, arrayIdent, tableNames));
        //创建方法
        return jcHelper.makeStaticVoidMethod("updateSQL", List.of(param), jcHelper.Block(buffer));
    }

    /**
     * cursorToData(Cursor cursor) 的方法
     */
    private JCTree.JCMethodDecl cursorToData(JCTree.JCClassDecl dec) {
        //创建 Cursor cursor 的形参
        JCTree.JCVariableDecl param = jcHelper.Params("cursor", CURSOR, dec);

        //返回值的类型
        JCTree.JCExpression returnType = jcHelper.Ident(simpleClassName);

        ListBuffer<JCTree.JCStatement> buffer = jcHelper.buffer();
        //Demo data = new Demo();
        buffer.append(jcHelper.BlockVariable("data", jcHelper.Ident(simpleClassName), jcHelper.NewClass(simpleClassName)));
        //data.setSQLiteID(cursor.getLong(cursor.getColumnIndex(SQLite.SQL_ID)));
        //cursor.getColumnIndex(SQLite.SQL_ID)
        JCTree.JCMethodInvocation idIndex = jcHelper.callMethod("cursor.getColumnIndex", jcHelper.Value(SQL_ID));
        JCTree.JCMethodInvocation idData = jcHelper.callMethod("cursor.getLong", idIndex);
        //data.setSQLiteID
        buffer.append(jcHelper.execMethod("data.setSQLiteID", idData));
//        buffer.append(jcHelper.ExecAssign(jcHelper.Access("data.aLong"),idData));

        for (String keySet : paramsMap.keySet()) {
            SQLParams params = paramsMap.get(keySet);
            JCTree.JCLiteral nameLiteral = jcHelper.Value(params.name);
            JCTree.JCMethodInvocation dataIndex = jcHelper.callMethod("cursor.getColumnIndex", nameLiteral);
            if (SQLUtils.isBoolean(params)) {
                JCTree.JCMethodInvocation dataInt = jcHelper.callMethod("cursor.getInt", dataIndex);
                JCTree.JCBinary binary = jcHelper.BinaryValue(JCTree.Tag.EQ, dataInt, 1);
                buffer.append(jcHelper.ExecAssign(jcHelper.Access("data." + keySet), binary));
            } else if (SQLUtils.isByte(params)) {
                JCTree.JCMethodInvocation data = jcHelper.callMethod("cursor.getInt", dataIndex);
                JCTree.JCTypeCast jcTypeCast = jcHelper.Cast(jcHelper.byteType(), data);
                buffer.append(jcHelper.ExecAssign(jcHelper.Access("data." + keySet), jcTypeCast));
            } else if (SQLUtils.isShort(params)) {
                JCTree.JCMethodInvocation data = jcHelper.callMethod("cursor.getInt", dataIndex);
                JCTree.JCTypeCast jcTypeCast = jcHelper.Cast(jcHelper.shortType(), data);
                buffer.append(jcHelper.ExecAssign(jcHelper.Access("data." + keySet), jcTypeCast));
            } else if (SQLUtils.isInt(params)) {
                JCTree.JCMethodInvocation data = jcHelper.callMethod("cursor.getInt", dataIndex);
                buffer.append(jcHelper.ExecAssign(jcHelper.Access("data." + keySet), data));
            } else if (SQLUtils.isLong(params)) {
                JCTree.JCMethodInvocation data = jcHelper.callMethod("cursor.getLong", dataIndex);
                buffer.append(jcHelper.ExecAssign(jcHelper.Access("data." + keySet), data));
            } else if (SQLUtils.isFloat(params)) {
                JCTree.JCMethodInvocation data = jcHelper.callMethod("cursor.getFloat", dataIndex);
                buffer.append(jcHelper.ExecAssign(jcHelper.Access("data." + keySet), data));
            } else if (SQLUtils.isDouble(params)) {
                JCTree.JCMethodInvocation data = jcHelper.callMethod("cursor.getDouble", dataIndex);
                buffer.append(jcHelper.ExecAssign(jcHelper.Access("data." + keySet), data));
            } else if (SQLUtils.isChar(params)) {
                JCTree.JCMethodInvocation data = jcHelper.callMethod("cursor.getInt", dataIndex);
                JCTree.JCTypeCast jcTypeCast = jcHelper.Cast(jcHelper.charType(), data);
                buffer.append(jcHelper.ExecAssign(jcHelper.Access("data." + keySet), jcTypeCast));
            } else if (SQLUtils.isString(params)) {
                JCTree.JCMethodInvocation data = jcHelper.callMethod("cursor.getString", dataIndex);
                buffer.append(jcHelper.ExecAssign(jcHelper.Access("data." + keySet), data));
            }
        }
        buffer.append(jcHelper.Return("data"));
        //创建方法
        return jcHelper.makeStaticMethod("cursorToData", returnType, List.of(param), jcHelper.Block(buffer));
    }

    /**
     * cursorToList(List<Demo> list, Cursor cursor) 的方法
     */
    private JCTree.JCMethodDecl cursorToList(JCTree.JCClassDecl dec) {
        //创建 Cursor cursor 的形参
        JCTree.JCVariableDecl param1 = jcHelper.MethodParams("list", jcHelper.ListCollection(simpleClassName), dec);
        JCTree.JCVariableDecl param2 = jcHelper.Params("cursor", CURSOR, dec);
        //cursorToData(cursor)
        JCTree.JCMethodInvocation value = jcHelper.callMethod("cursorToData", jcHelper.Ident("cursor"));
        // list.add(cursorToData(cursor))
        JCTree.JCExpressionStatement method = jcHelper.execMethod("list.add", value);
        //while (cursor.moveToNext())
        JCTree.JCMethodInvocation condition = jcHelper.callMethod("cursor", "moveToNext");
        JCTree.JCWhileLoop loop = jcHelper.WhileLoop(condition, method);
        //创建方法
        return jcHelper.makeStaticVoidMethod("cursorToList", List.of(param1, param2), jcHelper.Block(loop));
    }


    /****************** 创建成员变量/方法 *******************/

    /**
     * 创建数据库 _ID
     */
    private JCTree.JCVariableDecl createSQLId() {
        return jcHelper.Variate(Flags.PRIVATE, "_ID", jcHelper.Type2Expression(jcHelper.longType()), jcHelper.Value(0));
    }

    /**
     * 获取数据库 _ID getSQLiteID()
     */
    private JCTree.JCMethodDecl getSQLiteID() {
        //返回值的类型
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.longType());
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(SQLiteID == null ? "_ID" : SQLiteID));
        return jcHelper.makePublicMethod("getSQLiteID", returnType, block);
    }

    /**
     * 设置数据库 _ID setSQLiteID(long id)
     */
    private JCTree.JCMethodDecl setSQLiteID(JCTree.JCClassDecl dec) {
        //public void setSQLiteID(long id)
        JCTree.JCExpression jcExpression = jcHelper.Type2Expression(jcHelper.longType());
        JCTree.JCVariableDecl param = jcHelper.MethodParams("sql_id", jcExpression, dec);
        JCTree.JCExpression returnType = jcHelper.VoidExpression();
        JCTree.JCExpression access = jcHelper.Access("this.", SQLiteID == null ? "_ID" : SQLiteID);
        JCTree.JCIdent ident = jcHelper.Ident("sql_id");
        JCTree.JCExpression result;
        if (idIsLong) {
            result = ident;
        } else {
            result = jcHelper.Cast(jcHelper.intType(), ident);
        }
        JCTree.JCBlock block = jcHelper.Block(jcHelper.ExecAssign(access, result));
        return jcHelper.makePublicMethod("setSQLiteID", returnType, List.of(param), block);
    }

    /**
     * 判断是否保存 isSaved()
     */
    private JCTree.JCMethodDecl isSaved() {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.booleanType());
        JCTree.JCExpression getId = jcHelper.callMethod("getSQLiteID");
        JCTree.JCBinary binary = jcHelper.BinaryValue(JCTree.Tag.GT, getId, 0);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(binary));
        return jcHelper.makePublicMethod("isSaved", returnType, block);
    }

    /**
     * 清除保存状态 clearSavedState()
     */
    private JCTree.JCMethodDecl clearSavedState() {
        JCTree.JCExpressionStatement id = jcHelper.execMethod("setSQLiteID", jcHelper.Value(0));
        JCTree.JCBlock jcBlock = jcHelper.Block(id);
        return jcHelper.makePublicMethod("clearSavedState", jcHelper.VoidExpression(), jcBlock);
    }

    /**
     * SQLiteValues(ContentValues value)
     */
    private JCTree.JCMethodDecl SQLiteValues(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.VoidExpression();
        JCTree.JCVariableDecl param = jcHelper.Params("value", CONTENT_VALUES, dec);
        ListBuffer<JCTree.JCStatement> buffer = jcHelper.buffer();
        for (String keySet : paramsMap.keySet()) {
            SQLParams params = paramsMap.get(keySet);
            String consName = SQL_CLASS + "." + keySet;
            JCTree.JCExpression access = jcHelper.Access(consName);
            JCTree.JCIdent ident = jcHelper.Ident(keySet);

            if (SQLUtils.isByte(params)) {
                buffer.append(jcHelper.execMethod("value.put", access, ident));
            } else if (SQLUtils.isBoolean(params)) {
                JCTree.JCConditional value = jcHelper.ConditionalValue(ident, 1, 0);
                buffer.append(jcHelper.execMethod("value.put", access, value));
            } else if (SQLUtils.isShort(params)) {
                buffer.append(jcHelper.execMethod("value.put", access, ident));
            } else if (SQLUtils.isInt(params)) {
                buffer.append(jcHelper.execMethod("value.put", access, ident));
            } else if (SQLUtils.isLong(params)) {
                buffer.append(jcHelper.execMethod("value.put", access, ident));
            } else if (SQLUtils.isFloat(params)) {
                buffer.append(jcHelper.execMethod("value.put", access, ident));
            } else if (SQLUtils.isDouble(params)) {
                buffer.append(jcHelper.execMethod("value.put", access, ident));
            } else if (SQLUtils.isString(params)) {
                buffer.append(jcHelper.execMethod("value.put", access, ident));
            } else if (SQLUtils.isChar(params)) {
                JCTree.JCTypeCast cast = jcHelper.Cast(jcHelper.intType(), ident);
                buffer.append(jcHelper.execMethod("value.put", access, cast));
            }

        }
        JCTree.JCBlock block = jcHelper.Block(buffer);
        return jcHelper.makePublicMethod("SQLiteValues", returnType, List.of(param), block);
    }


    /**
     * 保存 public long insert(String tableName)
     */
    private JCTree.JCMethodDecl insert(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        List<JCTree.JCVariableDecl> parameter = getNameParameter(dec);
        JCTree.JCIdent name = jcHelper.Ident("name");
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "insert", jcHelper.NullValue(),name, thisIdent);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("insert", returnType, parameter, block);
    }

    /**
     * 保存 public long insertOrThrow(String tableName)
     */
    private JCTree.JCMethodDecl insertOrThrow(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        List<JCTree.JCVariableDecl> parameter = getNameParameter(dec);
        JCTree.JCIdent name = jcHelper.Ident("name");
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "insertOrThrow", jcHelper.NullValue(), name, thisIdent);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("insertOrThrow", returnType, parameter, block);
    }

    /**
     * 保存 public long insert()
     */
    private JCTree.JCMethodDecl insert() {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        JCTree.JCExpression access = DEFAULT_TABLE_FIELD;
        JCTree.JCMethodInvocation method = jcHelper.callMethod("insert", access);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("insert", returnType, block);
    }

    /**
     * 保存 public long insertOrThrow()
     */
    private JCTree.JCMethodDecl insertOrThrow() {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        JCTree.JCExpression access = DEFAULT_TABLE_FIELD;
        JCTree.JCMethodInvocation method = jcHelper.callMethod("insertOrThrow", access);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("insertOrThrow", returnType, block);
    }

    /**
     * 删除 public int delete(String tableName)
     */
    private JCTree.JCMethodDecl delete(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        List<JCTree.JCVariableDecl> parameter = getNameParameter(dec);
        JCTree.JCIdent name = jcHelper.Ident("name");
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "delete", jcHelper.NullValue(), name, thisIdent);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("delete", returnType, parameter, block);
    }

    /**
     * 删除 public int delete()
     */
    private JCTree.JCMethodDecl delete() {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        JCTree.JCExpression access = DEFAULT_TABLE_FIELD;
        JCTree.JCMethodInvocation method = jcHelper.callMethod("delete", access);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("delete", returnType, block);
    }


    /**
     * 保存 public long replace()
     */
    private JCTree.JCMethodDecl replace() {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        JCTree.JCExpression access = DEFAULT_TABLE_FIELD;
        JCTree.JCMethodInvocation method = jcHelper.callMethod("replace", access);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("replace", returnType, block);
    }

    /**
     * 保存 public long replaceOrThrow()
     */
    private JCTree.JCMethodDecl replaceOrThrow() {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        JCTree.JCExpression access = DEFAULT_TABLE_FIELD;
        JCTree.JCMethodInvocation method = jcHelper.callMethod("replaceOrThrow", access);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("replaceOrThrow", returnType, block);
    }

    /**
     * public long replace(String tableName)
     */
    private JCTree.JCMethodDecl replace(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        List<JCTree.JCVariableDecl> parameter = getNameParameter(dec);
        JCTree.JCIdent name = jcHelper.Ident("name");
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "replace", jcHelper.NullValue(), name, thisIdent);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("replace", returnType, parameter, block);
    }

    /**
     * public long replaceOrThrow(String tableName)
     */
    private JCTree.JCMethodDecl replaceOrThrow(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        List<JCTree.JCVariableDecl> parameter = getNameParameter(dec);
        JCTree.JCIdent name = jcHelper.Ident("name");
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "replaceOrThrow", jcHelper.NullValue(), name, thisIdent);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("replaceOrThrow", returnType, parameter, block);
    }

    /**
     * public int update(String tableName, long... ids)
     */
    private JCTree.JCMethodDecl update(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        // 生成方法参数
        JCTree.JCVariableDecl namePar = jcHelper.MethodParams("name", "String", dec);
        JCTree.JCVariableDecl idsPar = jcHelper.VariesArrayMethodParams("ids", jcHelper.LongVarargsArray(), dec);
        List<JCTree.JCVariableDecl> parameter = List.of(namePar, idsPar);

        JCTree.JCIdent name = jcHelper.Ident("name");
        JCTree.JCIdent ids = jcHelper.Ident("ids");
        JCTree.JCExpression clause = jcHelper.Access(SQL_UTILS,"SQL_WHERE_CLAUSE");
        JCTree.JCMethodInvocation longToString = jcHelper.callMethod(SQL_UTILS, "longToString", ids);
        JCTree.JCMethodInvocation method = jcHelper.callMethod( "update", name, clause,longToString);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("update", returnType, parameter, block);
    }

    /**
     * public int update(String whereClause, String... conditions)
     */
    private JCTree.JCMethodDecl update2(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        // 生成方法参数
        JCTree.JCVariableDecl namePar = jcHelper.MethodParams("whereClause", "String", dec);
        JCTree.JCVariableDecl strPar = jcHelper.VariesArrayMethodParams("conditions", jcHelper.StringVarargsArray(), dec);
        List<JCTree.JCVariableDecl> parameter = List.of(namePar, strPar);

        JCTree.JCIdent name = jcHelper.Ident("whereClause");
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCIdent conditions = jcHelper.Ident("conditions");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "update", jcHelper.NullValue(), DEFAULT_TABLE_FIELD, thisIdent, name, conditions);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("update", returnType, parameter, block);
    }

    /**
     * public int update(String tableName, String whereClause, String... conditions)
     */
    private JCTree.JCMethodDecl update3(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        // 生成方法参数
        JCTree.JCVariableDecl namePar = jcHelper.MethodParams("name", "String", dec);
        JCTree.JCVariableDecl wherePar = jcHelper.MethodParams("whereClause", "String", dec);
        JCTree.JCVariableDecl strPar = jcHelper.VariesArrayMethodParams("conditions", jcHelper.StringVarargsArray(), dec);
        List<JCTree.JCVariableDecl> parameter = List.of(namePar, wherePar, strPar);

        JCTree.JCIdent name = jcHelper.Ident("name");
        JCTree.JCIdent whereClause = jcHelper.Ident("whereClause");
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCIdent conditions = jcHelper.Ident("conditions");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "update", jcHelper.NullValue(), name, thisIdent, whereClause, conditions);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("update", returnType, parameter, block);
    }


    /**
     * public int update(long... ids)
     */
    private JCTree.JCMethodDecl update4(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        // 生成方法参数
        JCTree.JCVariableDecl idsPar = jcHelper.VariesArrayMethodParams("ids", jcHelper.LongVarargsArray(), dec);
        List<JCTree.JCVariableDecl> parameter = List.of(idsPar);

        JCTree.JCIdent ids = jcHelper.Ident("ids");
        JCTree.JCMethodInvocation method = jcHelper.callMethod("update", DEFAULT_TABLE_FIELD, ids);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("update", returnType, parameter, block);
    }

    /**
     * public int update(String tableName)
     */
    private JCTree.JCMethodDecl update5(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        JCTree.JCIdent name = jcHelper.Ident("name");
//        JCTree.JCMethodInvocation liteID = jcHelper.callMethod("getSQLiteID");
//        JCTree.JCMethodInvocation method = jcHelper.callMethod("update", name, liteID);
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "update", jcHelper.NullValue(), name,thisIdent);

        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("update", returnType, getNameParameter(dec), block);
    }

    /**
     * public int update()
     */
    private JCTree.JCMethodDecl update() {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
//        JCTree.JCMethodInvocation liteID = jcHelper.callMethod("getSQLiteID");
//        JCTree.JCMethodInvocation method = jcHelper.callMethod("update", liteID);
        JCTree.JCMethodInvocation method = jcHelper.callMethod("update", DEFAULT_TABLE_FIELD);

        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("update", returnType, block);
    }


    /**
     * public int updateOrThrow(String tableName, long... ids)
     */
    private JCTree.JCMethodDecl updateOrThrow(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        // 生成方法参数
        JCTree.JCVariableDecl namePar = jcHelper.MethodParams("name", "String", dec);
        JCTree.JCVariableDecl idsPar = jcHelper.VariesArrayMethodParams("ids", jcHelper.LongVarargsArray(), dec);
        List<JCTree.JCVariableDecl> parameter = List.of(namePar, idsPar);

        JCTree.JCIdent name = jcHelper.Ident("name");
        JCTree.JCIdent ids = jcHelper.Ident("ids");
        JCTree.JCExpression clause = jcHelper.Access(SQL_UTILS,"SQL_WHERE_CLAUSE");
        JCTree.JCMethodInvocation longToString = jcHelper.callMethod(SQL_UTILS, "longToString", ids);
        JCTree.JCMethodInvocation method = jcHelper.callMethod( "updateOrThrow", name, clause,longToString);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("updateOrThrow", returnType, parameter, block);
    }

    /**
     * public int updateOrThrow(String whereClause, String... conditions)
     */
    private JCTree.JCMethodDecl updateOrThrow2(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        // 生成方法参数
        JCTree.JCVariableDecl namePar = jcHelper.MethodParams("whereClause", "String", dec);
        JCTree.JCVariableDecl strPar = jcHelper.VariesArrayMethodParams("conditions", jcHelper.StringVarargsArray(), dec);
        List<JCTree.JCVariableDecl> parameter = List.of(namePar, strPar);

        JCTree.JCIdent name = jcHelper.Ident("whereClause");
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCIdent conditions = jcHelper.Ident("conditions");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "updateOrThrow", jcHelper.NullValue(), DEFAULT_TABLE_FIELD, thisIdent, name, conditions);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("updateOrThrow", returnType, parameter, block);
    }

    /**
     * public int updateOrThrow(String tableName, String whereClause, String... conditions)
     */
    private JCTree.JCMethodDecl updateOrThrow3(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        // 生成方法参数
        JCTree.JCVariableDecl namePar = jcHelper.MethodParams("name", "String", dec);
        JCTree.JCVariableDecl wherePar = jcHelper.MethodParams("whereClause", "String", dec);
        JCTree.JCVariableDecl strPar = jcHelper.VariesArrayMethodParams("conditions", jcHelper.StringVarargsArray(), dec);
        List<JCTree.JCVariableDecl> parameter = List.of(namePar, wherePar, strPar);

        JCTree.JCIdent name = jcHelper.Ident("name");
        JCTree.JCIdent whereClause = jcHelper.Ident("whereClause");
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCIdent conditions = jcHelper.Ident("conditions");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "updateOrThrow", jcHelper.NullValue(), name, thisIdent, whereClause, conditions);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("updateOrThrow", returnType, parameter, block);
    }


    /**
     * public int updateOrThrow(long... ids)
     */
    private JCTree.JCMethodDecl updateOrThrow4(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        // 生成方法参数
        JCTree.JCVariableDecl idsPar = jcHelper.VariesArrayMethodParams("ids", jcHelper.LongVarargsArray(), dec);
        List<JCTree.JCVariableDecl> parameter = List.of(idsPar);

        JCTree.JCIdent ids = jcHelper.Ident("ids");
        JCTree.JCMethodInvocation method = jcHelper.callMethod("updateOrThrow", DEFAULT_TABLE_FIELD, ids);
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("updateOrThrow", returnType, parameter, block);
    }

    /**
     * public int updateOrThrow(String tableName)
     */
    private JCTree.JCMethodDecl updateOrThrow5(JCTree.JCClassDecl dec) {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
        JCTree.JCIdent name = jcHelper.Ident("name");
//        JCTree.JCMethodInvocation liteID = jcHelper.callMethod("getSQLiteID");
//        JCTree.JCMethodInvocation method = jcHelper.callMethod("updateOrThrow", name, liteID);
        JCTree.JCIdent thisIdent = jcHelper.Ident("this");
        JCTree.JCMethodInvocation method = jcHelper.callMethod(SQL_UTILS, "updateOrThrow", jcHelper.NullValue(), name,thisIdent);

        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("updateOrThrow", returnType, getNameParameter(dec), block);
    }

    /**
     * public int updateOrThrow()
     */
    private JCTree.JCMethodDecl updateOrThrow() {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.intType());
//        JCTree.JCMethodInvocation liteID = jcHelper.callMethod("getSQLiteID");
//        JCTree.JCMethodInvocation method = jcHelper.callMethod("updateOrThrow", liteID);
        JCTree.JCMethodInvocation method = jcHelper.callMethod("updateOrThrow", DEFAULT_TABLE_FIELD);

        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(method));
        return jcHelper.makePublicMethod("updateOrThrow", returnType, block);
    }

    /**
     * public int updateOrThrow()
     */
    private JCTree.JCMethodDecl defaultTableName() {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.stringType());
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(DEFAULT_TABLE_FIELD));
        return jcHelper.makePublicMethod("defaultTableName", returnType, block);
    }
    /**
     * public int updateOrThrow()
     */
    private JCTree.JCMethodDecl uniqueColumn() {
        JCTree.JCExpression returnType = jcHelper.Type2Expression(jcHelper.stringType());
        JCTree.JCLiteral literal = jcHelper.Value(SQLUtils.getUniqueColumn(paramsMap));
        JCTree.JCBlock block = jcHelper.Block(jcHelper.Return(literal));
        return jcHelper.makePublicMethod("uniqueColumn", returnType, block);
    }

}
