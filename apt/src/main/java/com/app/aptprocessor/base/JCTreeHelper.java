package com.app.aptprocessor.base;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * JCStatement：     声明语法树节点，常见的子类如下
 * JCBlock：         语句块语法树节点
 * JCReturn：        return语句语法树节点
 * JCClassDecl：     类定义语法树节点
 * JCVariableDecl：  字段/变量定义语法树节点
 * JCMethodDecl：    方法定义语法树节点
 * JCModifiers：     访问标志语法树节点
 * JCExpression：    表达式语法树节点，常见的子类如下
 * JCAssign：        赋值语句语法树节点
 * JCIdent：         标识符语法树节点，可以是变量，类型，关键字等等
 */
public final class JCTreeHelper extends BaseJCTree {

    public JCTreeHelper(ProcessingEnvironment environment) {
        super(environment);
    }

    /*------------------------ Block ------------------------*/

    /**
     * 返回一个空的block
     */
    public JCTree.JCBlock EmptyBlock() {
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer();
        return treeMaker.Block(0, jcStatements.toList());
    }

    public JCTree.JCBlock Block(JCTree.JCStatement... jcStatement) {
        return treeMaker.Block(0, List.from(jcStatement));
    }

    public JCTree.JCBlock Block(ListBuffer<JCTree.JCStatement> jcStatements) {
        return treeMaker.Block(0, jcStatements.toList());
    }
    /*------------------------ Variate ------------------------*/
    //类变量：独立于方法之外的变量，用 static 修饰。
    //实例变量：独立于方法之外的变量，不过没有 static 修饰。
    //局部变量：类的方法中的变量。

    /**
     * 创建变量语句
     *
     * @param varSymbol 变量的类型
     * @param init      初始化语句,只有成员变量以及方法变量才有初始化值,在方法变量上没有初始化值
     */
    public JCTree.JCVariableDecl Variate(Symbol.VarSymbol varSymbol, JCTree.JCExpression init) {
        return treeMaker.VarDef(varSymbol, init);
    }

    /**
     * 创建变量语句
     *
     * @param modifiers 变量的修饰符
     * @param varName   变量的名称
     * @param classPath 变量的类型
     * @param init      初始化语句,只有成员变量以及方法变量才有初始化值,在方法变量上没有初始化值
     */
    public JCTree.JCVariableDecl VariateForSymbol(long modifiers, String varName, String classPath, JCTree.JCExpression init) {
        Symbol.VarSymbol varSymbol = makeVarSymbol(modifiers, varName, classPath);
        return Variate(varSymbol, init);
    }


    /**
     * 创建变量语句
     *
     * @param modifiers 修饰符
     * @param name      变量名称
     * @param varType   变量的类型
     * @param init      初始化语句,只有成员变量以及方法变量才有初始化值,在方法变量上没有初始化值
     */
    public JCTree.JCVariableDecl Variate(JCTree.JCModifiers modifiers, Name name,
                                         JCTree.JCExpression varType, JCTree.JCExpression init) {
        return treeMaker.VarDef(modifiers, name, varType, init);
    }


    /**
     * 创建变量语句
     *
     * @param modifiers 修饰符
     * @param name      变量名称
     * @param varType   变量的类型
     * @param init      初始化语句,只有成员变量以及方法变量才有初始化值,在方法变量上没有初始化值
     */
    public JCTree.JCVariableDecl Variate(long modifiers, String name, JCTree.JCExpression varType,
                                         JCTree.JCExpression init) {
        return Variate(treeMaker.Modifiers(modifiers), getName(name), varType, init);
    }

    /**
     * 创建变量语句
     *
     * @param name             变量名称
     * @param varTypeClassPath 变量的类型
     */
    public JCTree.JCVariableDecl Variate(long modifiers, String name, String varTypeClassPath, JCTree.JCExpression init) {
        return Variate(modifiers, name, Access(varTypeClassPath), init);
    }

    /**
     * 创建变量语句
     *
     * @param name             变量名称
     * @param varTypeClassPath 变量的类型,类的全路径,比如输入 java.lang.Runnable 等即可
     */
    public JCTree.JCVariableDecl Variate(long modifiers, String name, String varTypeClassPath) {
        return Variate(modifiers, name, varTypeClassPath, null);
    }


    /**
     * 声明成员变量并赋值
     *
     * @param modifiers 修饰符
     * @param name      成员变量名称
     * @param clazzName 变量的类型
     * @param init      给变量的赋值
     */
    public JCTree.JCVariableDecl MemberVariable(long modifiers, String name, String clazzName, JCTree.JCExpression init) {
        return Variate(modifiers, name, Access(clazzName), init);
    }

    /**
     * 声明实例变量(成员变量)并赋值
     *
     * @param modifiers 修饰符
     * @param name      成员变量名称
     * @param clazzName 变量的类型
     * @param init      给变量的赋值
     */
    public JCTree.JCVariableDecl MemberVariable(long modifiers, String name, String clazzName, Object init) {
        return MemberVariable(modifiers, name, clazzName, Value(init));
    }


    /**
     * 声明实例变量(成员变量)并赋值
     *
     * @param modifiers 修饰符
     * @param name      成员变量名称
     * @param clazz     变量的类型
     * @param value     给变量的赋值
     */
    public JCTree.JCVariableDecl MemberVariable(long modifiers, String name, Class clazz, Object value) {
        return MemberVariable(modifiers, name, clazz.getName(), value);
    }

    /**
     * 声明实例变量(成员变量)并赋值
     *
     * @param modifiers 修饰符
     * @param name      成员变量名称
     * @param clazzName 变量的类型
     * @param init      给变量的赋值
     */
    public JCTree.JCVariableDecl MemberVariableFromSymbol(long modifiers, String name, String clazzName, JCTree.JCExpression init) {
        return VariateForSymbol(modifiers, name, clazzName, init);
    }


    /**
     * 声明实例变量(成员变量)并赋值
     *
     * @param modifiers 修饰符
     * @param name      成员变量名称
     * @param clazz     变量的类型
     * @param value     给变量的赋值
     */
    public JCTree.JCVariableDecl MemberVariableFromSymbol(long modifiers, String name, Class clazz, JCTree.JCExpression value) {
        return VariateForSymbol(modifiers, name, clazz.getName(), value);
    }


    /**
     * 声明局部变量(方法块内变量)并赋值
     *
     * @param name 成员变量名称
     * @param type 变量的类型
     * @param init 给变量的赋值
     */
    public JCTree.JCVariableDecl BlockVariable(String name, JCTree.JCExpression type, JCTree.JCExpression init) {
        return Variate(0, name, type, init);
    }

    /**
     * 声明局部变量(方法块内变量)并赋值
     *
     * @param name      成员变量名称
     * @param clazzName 变量的类型
     * @param init      给变量的赋值
     */
    public JCTree.JCVariableDecl BlockVariable(String name, String clazzName, JCTree.JCExpression init) {
        return BlockVariable(name, Access(clazzName), init);
    }

    /**
     * 声明局部变量(方法块内变量)并赋值
     *
     * @param name      成员变量名称
     * @param clazzName 变量的类型
     * @param init      给变量的赋值
     */
    public JCTree.JCVariableDecl BlockVariable(String name, String clazzName, TypeTag type, Object init) {
        return BlockVariable(name, clazzName, Value(type, init));
    }


    /**
     * 声明局部变量(方法块内变量)并赋值
     *
     * @param name      成员变量名称
     * @param clazzName 变量的类型
     * @param init      给变量的赋值
     */
    public JCTree.JCVariableDecl BlockVariableFromSymbol(String name, String clazzName, JCTree.JCExpression init) {
        return VariateForSymbol(0, name, clazzName, init);
    }



    /*------------------------ Params ------------------------*/

    /**
     * 创建方法体参数,如果没有导包,需要使用该方法创建方法体参数,才能够自动导包
     *
     * @param paramsName 参数名称
     * @param varType    参数的类型
     * @param symbol     参数的所在类
     * @param relyTree   需要依赖的语法树
     */
    public JCTree.JCVariableDecl Params(Name paramsName, Type varType, Symbol symbol, JCTree relyTree) {
        //方法体参数的类型为 Flags.PARAMETER
        JCTree.JCVariableDecl param = treeMaker.Param(paramsName, varType, symbol);
        //此字段用于指明当前语法节点在语法树中的位置。分析字段生成，发现使用treeMark生成的POS都为固定值，实际此字段应与解析的element保持一致。
        //设置形参这一句不能少，不然会编译报错(java.lang.AssertionError: Value of x -1)
        param.pos = relyTree.pos;
        return param;
    }

    /**
     * 创建方法体参数 如果没有导包,需要使用该方法创建方法体参数,才能够自动导包
     *
     * @param paramsName 参数名称
     * @param varType    参数的类型
     * @param symbol     参数的所在类
     * @param relyTree   需要依赖的语法树
     */
    public JCTree.JCVariableDecl Params(String paramsName, Type varType, Symbol symbol, JCTree relyTree) {
        return Params(getName(paramsName), varType, symbol, relyTree);
    }

    /**
     * 创建方法体参数 如果没有导包,需要使用该方法创建方法体参数,才能够自动导包
     *
     * @param paramsName 参数名称
     * @param symbol     参数的所在类
     * @param relyTree   需要依赖的语法树
     */
    public JCTree.JCVariableDecl Params(Name paramsName, Symbol symbol, JCTree relyTree) {
        return Params(paramsName, symbol.type, symbol, relyTree);
    }

    /**
     * 创建方法体参数 如果没有导包,需要使用该方法创建方法体参数,才能够自动导包
     *
     * @param paramsName 参数名称
     * @param symbol     参数的所在类
     * @param relyTree   需要依赖的语法树
     */
    public JCTree.JCVariableDecl Params(String paramsName, Symbol symbol, JCTree relyTree) {
        return Params(getName(paramsName), symbol, relyTree);
    }

    /**
     * 创建方法体参数 如果没有导包,需要使用该方法创建方法体参数,才能够自动导包
     *
     * @param paramsName 参数名称
     * @param classPath  参数的所在类的全路径
     * @param relyTree   需要依赖的语法树
     */
    public JCTree.JCVariableDecl Params(String paramsName, String classPath, JCTree relyTree) {
        return Params(getName(paramsName), makeClassSymbol(classPath), relyTree);
    }

    /**
     * 创建方法体参数
     *
     * @param name     参数名称
     * @param varType  参数的类型
     * @param relyTree 需要依赖的语法树
     */
    public JCTree.JCVariableDecl MethodParams(String name, JCTree.JCExpression varType, JCTree relyTree) {
        //方法体参数的类型为 Flags.PARAMETER
        JCTree.JCVariableDecl dec = Variate(Flags.PARAMETER, name, varType, null);
        //此字段用于指明当前语法节点在语法树中的位置。分析字段生成，发现使用treeMark生成的POS都为固定值，实际此字段应与解析的element保持一致。
        //设置形参这一句不能少，不然会编译报错(java.lang.AssertionError: Value of x -1)
        dec.pos = relyTree.pos;
        return dec;
    }

    /**
     * 创建方法体参数
     *
     * @param name        参数名称
     * @param varTypeName 参数的类型
     * @param relyTree    需要依赖的语法树
     */
    public JCTree.JCVariableDecl MethodParams(String name, String varTypeName, JCTree relyTree) {
        return MethodParams(name, Ident(varTypeName), relyTree);
    }


    /**
     * 创建方法体参数
     *
     * @param name         参数名称
     * @param varTypeClass 参数的类型
     * @param relyTree     需要依赖的语法树
     */
    public JCTree.JCVariableDecl MethodParams(String name, Class varTypeClass, JCTree relyTree) {
        return MethodParams(name, varTypeClass.getName(), relyTree);
    }

    /**
     * 创建方法体 可变数组的参数
     *
     * @param name     参数名称
     * @param varType  参数的类型
     * @param relyTree 需要依赖的语法树
     */
    public JCTree.JCVariableDecl VariesArrayMethodParams(String name, JCTree.JCArrayTypeTree varType, JCTree relyTree) {
        //VARARGS 表示这是一个可变数组参数 Flags.PARAMETER表示是方法参数
        JCTree.JCVariableDecl dec = MethodParams(name, varType, relyTree);
        dec.mods = treeMaker.Modifiers(Flags.VARARGS | Flags.PARAMETER);
        return dec;
    }

    /*------------------------ Return ------------------------*/

    /**
     * 返回值
     *
     * @param expr 返回的值的表达式
     */
    public JCTree.JCReturn Return(JCTree.JCExpressionStatement expr) {
        return treeMaker.Return(expr.getExpression());
    }

    /**
     * 返回值
     *
     * @param expr 返回的值的表达式
     */
    public JCTree.JCReturn Return(JCTree.JCExpression expr) {
        return treeMaker.Return(expr);
    }

    /**
     * 构建返回值
     *
     * @param name
     */
    public JCTree.JCReturn Return(String name) {
        return treeMaker.Return(Ident(name));
    }

    /**
     * 构建返回值
     *
     * @param name
     */
    public JCTree.JCReturn Return(Name name) {
        return treeMaker.Return(Ident(name));
    }

    /*------------------------ Access 访问方法/域/常量 ------------------------*/

    /**
     * 创建 域/方法 的多级访问(这里的方法访问只是取到名字，方法的调用需要用TreeMaker.Apply),
     * 方法的标识只能是最后一个(注意,如果是调用其他类中的常量,必须使用该方法,makeType方法不可行)
     * 例如:传入 java.lang.String.valueOf ,即可调用 String.valueOf 方法
     * 传入 java.lang.String ,即可为创建String的类型
     * 例如 com.sun.tools.javac.code.Flags.PUBLIC 为调用Flags.PUBLIC 常量
     *
     * @param components 访问方法/域/常量的全路径
     */
    public JCTree.JCExpression Access(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = Ident(componentArray[0]);
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getName(componentArray[i]));
        }
        return expr;
    }

    /**
     * 创建 域/方法/常量 的多级访问
     *
     * @param clazzName  类的名称
     * @param methodName 方法名
     */
    public JCTree.JCExpression Access(String clazzName, String methodName) {
        return treeMaker.Select(Access(clazzName), getName(methodName));
    }

    /**
     * 创建 域/方法/常量 的多级访问
     *
     * @param clazz      类
     * @param methodName 方法名
     */
    public JCTree.JCExpression Access(Class clazz, String methodName) {
        return Access(clazz.getName(), methodName);
    }

    /**
     * 调用类的方法
     * Access(LayoutInflaterExpression, "inflate"),为调用LayoutInflater的inflate的方法
     *
     * @param jcExpression 需要调用的类的表达式
     * @param components   需要调用的方法的名称
     */
    public JCTree.JCExpression Access(JCTree.JCExpression jcExpression, Name components) {
        return treeMaker.Select(jcExpression, components);
    }

    /**
     * 调用方法
     *
     * @param jcExpression 需要调用的类的表达式
     * @param components   需要调用的方法的名称
     */
    public JCTree.JCExpression Access(JCTree.JCExpression jcExpression, String components) {
        return Access(jcExpression, getName(components));
    }


    /*------------------------ Ident 标识符(变量，类型，关键字)  ------------------------*/


    /**
     * 创建标识符语法树节点(变量，类型，关键字)
     */
    public JCTree.JCIdent Ident(Name components) {
        return treeMaker.Ident(components);
    }

    /**
     * 根据已有的Symbol符号来 创建标识符语法树节点(变量，类型，关键字)
     */
    public JCTree.JCIdent Ident(Symbol symbol) {
        return treeMaker.Ident(symbol);
    }

    /**
     * 根据 字段/变量 表达式 创建标识符语法树节点(变量，类型，关键字)
     *
     * @dec 变量表达式
     */
    public JCTree.JCExpression Ident(JCTree.JCVariableDecl dec) {
        return treeMaker.Ident(dec);
    }

    /**
     * 创建标识符语法树节点(变量，类型，关键字)
     *
     * @param components 变量，类型，关键字等的名称
     */
    public JCTree.JCIdent Ident(String components) {
        return Ident(getName(components));
    }

    /**
     * 创建标识符语法树节点
     *
     * @param clazz 需要创建的类型的class
     */
    public JCTree.JCIdent Ident(Class clazz) {
        return Ident(clazz.getName());
    }


    /**
     * 创建String type的变量声明
     */
    public JCTree.JCIdent StringIdent() {
        return Ident("String");
    }

    /*------------------------ Literal 常量/值 ------------------------*/

    /**
     * 根据值来创建常量/值
     */
    public JCTree.JCLiteral Value(Object value) {
        if (value == null) {
            return NullValue();
        }
        return treeMaker.Literal(value);
    }

    /**
     * 根据类型,创建表达
     */
    public JCTree.JCLiteral Value(TypeTag type, Object value) {
        return treeMaker.Literal(type, value);
    }

    /**
     * null的值的表达结果
     */
    public JCTree.JCLiteral NullValue() {
        return Value(TypeTag.BOT, null);
    }

    /*------------------------ Type  ------------------------*/


    /**
     * 根据Type类型创建类型变量表达式
     */
    public JCTree.JCExpression Type2Expression(Type type) {
        return treeMaker.Type(type);
    }

    /**
     * 创建java.util.List 类型/变量 表达式
     */
    public JCTree.JCExpression ListExpression() {
        return Type2Expression(symtab.listType);
    }

    /**
     * 创建java.lang.String 类型/变量 表达式
     */
    public JCTree.JCExpression StringExpression() {
        return Type2Expression(symtab.stringType);
    }

    /*------------------------ TypeIdent  ------------------------*/

    /**
     * 空值的表达式
     */
    public JCTree.JCPrimitiveTypeTree VoidExpression() {
        return treeMaker.TypeIdent(TypeTag.VOID);
    }

    /**
     * 创建基本数据类型标识符
     *
     * @param components
     */
    public JCTree.JCPrimitiveTypeTree PrimitiveExpression(TypeTag components) {
        return treeMaker.TypeIdent(components);
    }


    /*------------------------ Array 数组  ------------------------*/

    /**
     * 创建一个数组变量声明
     */
    public JCTree.JCArrayTypeTree Array(JCTree.JCExpression var1) {
        return treeMaker.TypeArray(var1);
    }

    /**
     * 创建一个字符串数组变量声明
     */
    public JCTree.JCArrayTypeTree StringArray() {
        return treeMaker.TypeArray(StringIdent());
    }


    /**
     * 创建一个字符串可变数组变量声明
     */
    public JCTree.JCArrayTypeTree StringVarargsArray() {
        return VarargsArray(stringType());
    }
    /**
     * 创建一个Long可变数组变量声明
     */
    public JCTree.JCArrayTypeTree LongVarargsArray() {
        return VarargsArray(longType());
    }
    /**
     * 创建一个字符串可变数组变量声明
     */
    public JCTree.JCArrayTypeTree VarargsArray(Type type) {
        JCTree.JCArrayTypeTree jcArrayTypeTree = treeMaker.TypeArray(Type2Expression(type));
        jcArrayTypeTree.setType(varargsArrayType(type));
        return jcArrayTypeTree;
    }

    /**
     * new数组
     *
     * @param arrayType 数组的类的表达式,比如 需要new String[1],则arrayType为String的类型表达式
     * @param initSize  数组的初始化数组 比如 new int[1]; initNumber[0]为1的表达式
     * @param initValue 数组的初始化数值,比如 new int[]{1,2}; initValue[0]为1的表达式
     * @return
     */
    public JCTree.JCNewArray newArray(JCTree.JCExpression arrayType, List<JCTree.JCExpression> initSize, List<JCTree.JCExpression> initValue) {
        return treeMaker.NewArray(arrayType, initSize, initValue);
    }

    /**
     * new数组
     *
     * @param arrayType 数组的类的表达式 需要new String[1],则arrayType为String的类型表达式
     * @param initSize  数组的初始化数组 比如 new int[1]; initNumber[0]为1的表达式
     * @return
     */
    public JCTree.JCNewArray newArrayInitSize(JCTree.JCExpression arrayType, List<JCTree.JCExpression> initSize) {
        return newArray(arrayType, check(initSize), null);
    }

    /**
     * new数组
     *
     * @param arrayType 数组的类的表达式 需要new String[1],则arrayType为String的类型表达式
     * @param initSize  数组的初始化数组 比如 new int[1]; initNumber[0]为1的表达式
     * @return
     */
    public JCTree.JCNewArray newArrayInitSize(JCTree.JCExpression arrayType, JCTree.JCExpression... initSize) {
        return newArrayInitSize(arrayType, List.from(initSize));
    }

    /**
     * new数组
     *
     * @param initSize 数组的初始化数组 比如 new int[1]; initNumber[0]为1的表达式
     * @return
     */
    public JCTree.JCNewArray newStringArrayInitSize(List<JCTree.JCExpression> initSize) {
        return newArrayInitSize(StringIdent(), initSize);
    }

    /**
     * new String[1]; 数组
     *
     * @param initSize 数组的初始化数组 比如 new int[1]; initNumber[0]为1的表达式
     * @return
     */
    public JCTree.JCNewArray newStringArrayInitSize(JCTree.JCExpression... initSize) {
        return newStringArrayInitSize(List.from(initSize));
    }

    /**
     * new数组
     *
     * @param arrayType 数组的类的表达式 需要new String[1],则arrayType为String的类型表达式
     * @param initSize  数组的初始化数组 比如 new int[1]; initNumber[0]为1的表达式
     * @return
     */
    public JCTree.JCNewArray newArrayInitSize(JCTree.JCExpression arrayType, int initSize) {
        return newArrayInitSize(arrayType, Value(TypeTag.INT, initSize));
    }

    /**
     * new数组
     *
     * @param arrayType 数组的类的表达式 需要new String[1],则arrayType为String的类型表达式
     * @param initValue 数组的初始化数值,比如 new int[]{1,2}; initValue[0]为1的表达式
     * @return
     */
    public JCTree.JCNewArray newArrayInitValue(JCTree.JCExpression arrayType, List<JCTree.JCExpression> initValue) {
        return newArray(arrayType, null, check(initValue));
    }

    /**
     * new数组
     *
     * @param arrayType 数组的类的表达式 需要new String[1],则arrayType为String的类型表达式
     * @param initValue 数组的初始化数值,比如 new int[]{1,2}; initValue[0]为1的表达式
     * @return
     */
    public JCTree.JCNewArray newArrayInitValue(JCTree.JCExpression arrayType, JCTree.JCExpression... initValue) {
        return newArrayInitValue(arrayType, List.from(initValue));
    }

    /*------------------------ NewClass new一个对象  ------------------------*/

    /**
     * 创建一个对象
     *
     * @param typeArgs 参数类型列表
     * @param clazz    待创建对象的类型
     * @param args     参数列表
     * @param classDef 类定义 一般为null
     * @return
     */
    public JCTree.JCNewClass NewClass(List<JCTree.JCExpression> typeArgs, JCTree.JCExpression clazz,
                                      List<JCTree.JCExpression> args, JCTree.JCClassDecl classDef) {
        return treeMaker.NewClass(null, typeArgs, clazz, args, classDef);
    }

    /**
     * 创建一个对象
     *
     * @param typeArgs 参数类型列表
     * @param clazz    待创建对象的类型
     * @param args     参数列表
     * @return
     */
    public JCTree.JCNewClass NewClass(List<JCTree.JCExpression> typeArgs, JCTree.JCExpression clazz,
                                      List<JCTree.JCExpression> args) {
        return NewClass(typeArgs, clazz, args, null);
    }

    /**
     * 创建一个对象
     *
     * @param clazz 待创建对象的类型
     * @param args  参数列表
     * @return
     */
    public JCTree.JCNewClass NewClass(JCTree.JCExpression clazz, List<JCTree.JCExpression> args) {
        return NewClass(List.<JCTree.JCExpression>nil(), clazz, args);
    }

    /**
     * 创建一个对象
     *
     * @param clazz 待创建对象的类型
     * @param args  参数列表
     * @return
     */
    public JCTree.JCNewClass NewClass(JCTree.JCExpression clazz, JCTree.JCExpression... args) {
        return NewClass(clazz, List.from(args));
    }


    /**
     * 创建一个对象
     *
     * @param clazz 待创建对象的类型
     * @param args  参数列表
     * @return
     */
    public JCTree.JCNewClass NewClass(String clazz, List<JCTree.JCExpression> args) {
        return NewClass(this.Access(clazz), args);
    }

    /**
     * 创建一个对象
     *
     * @param clazz 待创建对象的类型
     * @param args  参数列表
     * @return
     */
    public JCTree.JCNewClass NewClass(String clazz, JCTree.JCExpression... args) {
        return NewClass(this.Access(clazz), List.from(args));
    }

    /**
     * 创建一个对象
     *
     * @param clazz 待创建对象的类型
     * @param args  参数列表
     * @return
     */
    public JCTree.JCNewClass NewClass(Class clazz, List<JCTree.JCExpression> args) {
        return NewClass(this.Access(clazz.getName()), args);
    }

    /**
     * 创建一个对象
     *
     * @param clazz 待创建对象的类型
     * @param args  参数列表
     * @return
     */
    public JCTree.JCNewClass NewClass(Class clazz, JCTree.JCExpression... args) {
        return NewClass(this.Access(clazz.getName()), List.from(args));
    }


    /*------------------------ Assign 赋值  ------------------------*/

    /**
     * 给表达式赋值,lhs = rhs,并生成可执行方法
     */
    public JCTree.JCExpressionStatement ExecAssign(JCTree.JCExpression lhs, JCTree.JCExpression rhs) {
        return treeMaker.Exec(treeMaker.Assign(lhs, rhs));
    }

    /**
     * 给表达式赋值,lhs = value,并生成可执行方法
     */
    public JCTree.JCExpressionStatement ExecAssign(JCTree.JCExpression lhs, TypeTag type, Object value) {
        return ExecAssign(lhs, Value(type, value));
    }

    /**
     * 创建变量并赋值 生成执行方法
     */
    public JCTree.JCExpressionStatement ExecAssign(String name, TypeTag type, Object value) {
        return ExecAssign(Ident(name), type, value);
    }

    /*------------------------ Collection 集合  ------------------------*/

    /**
     * 创建集合的标识符语法树节点
     *
     * @param type      集合类型
     * @param typePrams 泛型参数
     */
    public JCTree.JCTypeApply Collection(JCTree.JCExpression type, JCTree.JCExpression... typePrams) {
        return treeMaker.TypeApply(type, List.from(typePrams));
    }

    /**
     * 创建集合的标识符语法树节点
     *
     * @param type      集合类型
     * @param typePrams 泛型参数
     */
    public JCTree.JCTypeApply Collection(Name type, JCTree.JCExpression... typePrams) {
        return Collection(Ident(type), typePrams);
    }

    /**
     * 创建集合的标识符语法树节点
     *
     * @param type      集合类型
     * @param typePrams 泛型参数
     */
    public JCTree.JCTypeApply Collection(Name type, Name... typePrams) {
        JCTree.JCExpression[] jcExpressions = new JCTree.JCExpression[typePrams.length];
        for (int i = 0; i < typePrams.length; i++) {
            jcExpressions[i] = Ident(typePrams[i]);
        }
        return Collection(type, jcExpressions);
    }

    /**
     * 创建集合的标识符语法树节点
     *
     * @param type      集合类型
     * @param typePrams 泛型参数
     */
    public JCTree.JCTypeApply Collection(String type, String... typePrams) {
        JCTree.JCExpression[] jcExpressions = new JCTree.JCExpression[typePrams.length];
        for (int i = 0; i < typePrams.length; i++) {
            jcExpressions[i] = Ident(typePrams[i]);
        }
        return Collection(Access(type), jcExpressions);
    }

    /**
     * 创建集合的标识符语法树节点
     *
     * @param typePrams 泛型参数
     */
    public JCTree.JCTypeApply ListCollection(String typePrams) {
        return Collection("java.util.List", typePrams);
    }

    /**
     * 创建集合的标识符语法树节点
     *
     * @param typePrams 泛型参数
     */
    public JCTree.JCTypeApply SetCollection(String typePrams) {
        return Collection("java.util.Set", typePrams);
    }
    /*------------------------ WhileLoop While循环  ------------------------*/

    /**
     * 创建While循环
     *
     * @param condition While循环条件
     * @param body      While循环内部执行语句
     * @return
     */
    public JCTree.JCWhileLoop WhileLoop(JCTree.JCExpression condition, JCTree.JCStatement body) {
        return treeMaker.WhileLoop(condition, body);
    }

    /**
     * 创建While循环
     *
     * @param condition While循环条件
     * @param body      While循环内部执行语句
     * @return
     */
    public JCTree.JCWhileLoop WhileLoop(JCTree.JCExpression condition, ListBuffer<JCTree.JCStatement> body) {
        return WhileLoop(condition, Block(body));
    }


    /*------------------------ makeMethod 创建方法  ------------------------*/

    /**
     * 创建一个新的方法
     *
     * @param modifiers  访问标志
     * @param methodName 方法名
     * @param returnType 返回值类型
     * @param params     参数列表
     * @param body       方法的块
     */
    public JCTree.JCMethodDecl makeNewMethod(long modifiers, String methodName, JCTree.JCExpression returnType,
                                             List params, JCTree.JCBlock body) {
        return treeMaker.MethodDef(
                treeMaker.Modifiers(modifiers),//修饰符
                names.fromString(methodName), //方法名
                returnType,   //返回类型
                List.<JCTree.JCTypeParameter>nil(),  //泛型参数
                check(params),   //参数列表
                List.<JCTree.JCExpression>nil(), //抛出异常列表
                body, //方法体
                null);  //默认值
    }

    /**
     * 创建一个新的方法
     *
     * @param modifiers  访问标志
     * @param methodName 方法名
     * @param returnType 返回值类型
     * @param params     参数列表
     * @param body       方法的块
     */
    public JCTree.JCMethodDecl makeNewMethod(long modifiers, String methodName, String returnType,
                                             List<JCTree.JCVariableDecl> params, JCTree.JCBlock body) {
        return makeNewMethod(modifiers, methodName, Ident(returnType), params, body);
    }


    /**
     * 创建一个public static 没有返回值的方法
     *
     * @param methodName 方法名
     * @param params     参数列表
     * @param body       方法的块
     */
    public JCTree.JCMethodDecl makeStaticVoidMethod(String methodName, List<JCTree.JCVariableDecl> params,
                                                    JCTree.JCBlock body) {
        return makeStaticMethod(methodName, VoidExpression(), params, body);
    }

    /**
     * 创建一个public static 没有返回值的方法
     *
     * @param body       方法体语句
     * @param methodName 方法名
     */
    public JCTree.JCMethodDecl makeStaticVoidMethod(String methodName, JCTree.JCBlock body) {
        return makeStaticVoidMethod(methodName, null, body);
    }

    /**
     * 创建一个public static 的方法
     *
     * @param body       方法体语句
     * @param methodName 方法名
     * @param params     参数列表
     */
    public JCTree.JCMethodDecl makeStaticMethod(String methodName, JCTree.JCExpression returnType,
                                                List<JCTree.JCVariableDecl> params, JCTree.JCBlock body) {
        final int modifiers = Flags.PUBLIC | Flags.STATIC;
        return makeNewMethod(modifiers, methodName, returnType, params, body);
    }


    /**
     * 创建一个public static 的方法
     *
     * @param body       方法体语句
     * @param methodName 方法名
     */
    public JCTree.JCMethodDecl makeStaticMethod(String methodName, JCTree.JCExpression returnType,
                                                JCTree.JCBlock body) {
        return makeStaticMethod(methodName, returnType, null, body);
    }

    /**
     * 创建一个public 没有返回值的方法
     *
     * @param body       方法体语句
     * @param methodName 方法名
     * @param params     参数列表
     */
    public JCTree.JCMethodDecl makePublicVoidMethod(String methodName, List<JCTree.JCVariableDecl> params,
                                                    JCTree.JCBlock body) {
        return makeNewMethod(Flags.PUBLIC, methodName, VoidExpression(), params, body);
    }

    /**
     * 创建一个public 没有返回值的方法
     *
     * @param body       方法体语句
     * @param methodName 方法名
     */
    public JCTree.JCMethodDecl makePublicVoidMethod(String methodName, JCTree.JCBlock body) {
        return makePublicVoidMethod(methodName, null, body);
    }


    /**
     * 创建一个public 的方法
     *
     * @param body       方法体语句
     * @param methodName 方法名
     * @param params     参数列表
     */
    public JCTree.JCMethodDecl makePublicMethod(String methodName, JCTree.JCExpression returnType,
                                                List<JCTree.JCVariableDecl> params, JCTree.JCBlock body) {
        return makeNewMethod(Flags.PUBLIC, methodName, returnType, params, body);
    }

    /**
     * 创建一个public 的方法
     *
     * @param body       方法体语句
     * @param methodName 方法名
     */
    public JCTree.JCMethodDecl makePublicMethod(String methodName, JCTree.JCExpression returnType,
                                                JCTree.JCBlock body) {
        return makePublicMethod(methodName, returnType, null, body);
    }

    /*------------------------ TypeCast 类型强转  ------------------------*/

    /**
     * 类型强转
     *
     * @param type 类型
     * @param expr 强转表达式
     * @return
     */
    public JCTree.JCTypeCast Cast(Type type, JCTree.JCExpression expr) {
        return treeMaker.TypeCast(type, expr);
    }

    /**
     * 类型强转
     *
     * @param type 类型
     * @param expr 强转表达式
     * @return
     */
    public JCTree.JCTypeCast CastIdent(Type type, String expr) {
        return treeMaker.TypeCast(type, Ident(expr));
    }

    /**
     * 类型强转
     *
     * @param type 类型
     * @param expr 强转表达式
     * @return
     */
    public JCTree.JCTypeCast CastAccess(Type type, String expr) {
        return treeMaker.TypeCast(type, Access(expr));
    }

    /*------------------------ JCConditional 三目运算符  ------------------------*/

    /**
     * 三目运算符
     * @param condition 条件表达式
     * @param trueExpr 左表达式
     * @param falseExpr 右表达式
     * @return
     */
    public JCTree.JCConditional Conditional(JCTree.JCExpression condition, JCTree.JCExpression trueExpr,
                                            JCTree.JCExpression falseExpr) {
        return treeMaker.Conditional(condition, trueExpr, falseExpr);
    }

    /**
     * 三目运算符
     * @param condition 条件表达式
     * @param trueValue 左表达式的值
     * @param falseValue 右表达式
     * @return
     */
    public JCTree.JCConditional ConditionalValue(JCTree.JCExpression condition,Object trueValue,
                                                 Object falseValue) {
        return treeMaker.Conditional(condition, Value(trueValue), Value(falseValue));
    }

    /*------------------------ Binary 将语句分为二叉结构/二元运算符  ------------------------*/

    /**
     * 将语句使用二元运算符分为二叉结构
     *
     * @param tag       二元运算符
     * @param leftExpr  左边的表达式
     * @param rightExpr 右边的表达式
     * @return
     */
    public JCTree.JCBinary Binary(JCTree.Tag tag, JCTree.JCExpression leftExpr, JCTree.JCExpression rightExpr) {
        return treeMaker.Binary(tag, leftExpr, rightExpr);
    }

    /**
     * 将语句使用二元运算符分为二叉结构
     *
     * @param tag       二元运算符
     * @param leftExpr  左边的表达式
     * @param rightExpr 右边的表达式的值
     * @return
     */
    public JCTree.JCBinary BinaryValue(JCTree.Tag tag, JCTree.JCExpression leftExpr, Object rightExpr) {
        return treeMaker.Binary(tag, leftExpr, Value(rightExpr));
    }

    /**
     * 将语句使用二元运算符分为二叉结构
     *
     * @param tag       二元运算符
     * @param leftExpr  左边的表达式
     * @param type      右边的表达式的值的类型
     * @param rightExpr 右边的表达式
     * @return
     */
    public JCTree.JCBinary BinaryValue(JCTree.Tag tag, JCTree.JCExpression leftExpr, TypeTag type, Object rightExpr) {
        return treeMaker.Binary(tag, leftExpr, Value(type, rightExpr));
    }

    /*------------------------ callMethod 调用方法  ------------------------*/

    /**
     * 创建方法调用
     *
     * @param typeArgs 参数类型列表
     * @param method   方法的表达式
     * @param args     调用该方法需要传入的参数
     */
    public JCTree.JCMethodInvocation callMethod(List<JCTree.JCExpression> typeArgs, JCTree.JCExpression method,
                                                List<JCTree.JCExpression> args) {
        return treeMaker.Apply(typeArgs, method, check(args));
    }

    /**
     * 创建方法调用
     *
     * @param method 方法的表达式
     * @param args   调用该方法需要传入的参数
     */
    public JCTree.JCMethodInvocation callMethod(JCTree.JCExpression method, List<JCTree.JCExpression> args) {
        return callMethod(List.<JCTree.JCExpression>nil(), method, args);
    }

    /**
     * 调用某个类或变量的方法
     *
     * @param classPathAndMethodName 方法的语句,必须为:类的全路径.方法名
     *                               例如:java.lang.String.valueOf
     *                               如果上文中有变量 var 则传入 var.indexOf
     * @param args                   调用该方法需要传入的参数
     */
    public JCTree.JCMethodInvocation callMethod(List<JCTree.JCExpression> typeArgs, String classPathAndMethodName,
                                                List<JCTree.JCExpression> args) {
        return callMethod(typeArgs, Access(classPathAndMethodName), args);//参数列表
    }


    /**
     * 调用某个类或变量的方法
     *
     * @param classOrVarName 调用的类的路径,必须为:类的全路径/或者为某个变量的名称
     *                       例如:java.lang.String
     * @param methodName     方法的名称,例如valueOf
     * @param args           调用该方法需要传入的参数
     */
    public JCTree.JCMethodInvocation callMethod(List<JCTree.JCExpression> typeArgs, String classOrVarName,
                                                String methodName, List<JCTree.JCExpression> args) {
        return callMethod(typeArgs, Access(classOrVarName, methodName), args);
    }

    /**
     * 调用某个类或变量的方法
     *
     * @param classOrVarName 调用的类的路径,必须为:类的全路径/或者为某个变量的名称
     *                       例如:java.lang.String
     * @param methodName     方法的名称,例如valueOf
     * @param args           调用该方法需要传入的参数
     */
    public JCTree.JCMethodInvocation callMethod(String classOrVarName, String methodName,
                                                List<JCTree.JCExpression> args) {
        return callMethod(Access(classOrVarName, methodName), args);
    }

    /**
     * 调用某个类或变量的方法
     *
     * @param classOrVarName 调用的类的路径,必须为:类的全路径
     *                       例如:java.lang.String
     * @param methodName     方法的名称,例如valueOf
     * @param args           调用该方法需要传入的参数
     */
    public JCTree.JCMethodInvocation callMethod(String classOrVarName, String methodName, JCTree.JCExpression... args) {
        return callMethod(classOrVarName, methodName, List.from(args));//参数列表
    }

    /**
     * 调用某个类或变量的方法
     *
     * @param classPathAndMethodName 方法的语句,必须为:类的全路径.方法名
     *                               例如:java.lang.String.valueOf
     *                               如果上文中有变量 var 则传入 var.indexOf
     * @param args                   调用该方法需要传入的参数
     */
    public JCTree.JCMethodInvocation callMethod(String classPathAndMethodName, List<JCTree.JCExpression> args) {
        return callMethod(Access(classPathAndMethodName), args);//参数列表
    }


    /**
     * 调用某个类或变量的方法
     *
     * @param classPathAndMethodName 方法的语句,必须为:类的全路径.方法名
     *                               例如:java.lang.String.format
     *                               如果上文中有变量 var 则传入 var.indexOf
     * @param args                   调用该方法需要传入的参数
     */
    public JCTree.JCMethodInvocation callMethod(String classPathAndMethodName, JCTree.JCExpression... args) {
        return callMethod(classPathAndMethodName, List.from(args));//参数列表
    }


    /**
     * 调用某个类的静态方法
     *
     * @param clazz      调用的类
     * @param methodName 方法的名称,例如valueOf
     * @param args       调用该方法需要传入的参数
     */
    public JCTree.JCMethodInvocation callMethod(Class clazz, String methodName, List<JCTree.JCExpression> args) {
        return callMethod(Access(clazz, methodName), args);//参数列表
    }

    /**
     * 调用某个类的静态方法
     *
     * @param clazz      调用的类
     * @param methodName 方法的名称,例如valueOf
     * @param args       调用该方法需要传入的参数
     */
    public JCTree.JCMethodInvocation callMethod(Class clazz, String methodName, JCTree.JCExpression... args) {
        return callMethod(clazz, methodName, List.from(args));//参数列表
    }

    /**
     * 调用super的方法
     *
     * @param symbol              父类的方法
     * @param isRessetAnnotations 是否需要清理参数中和方法中的注解
     */
    public JCTree.JCMethodInvocation callSuperMethod(Symbol.MethodSymbol symbol, boolean isRessetAnnotations) {
        if (isRessetAnnotations) {
            symbol.resetAnnotations();
        }
        JCTree.JCExpression superExpression = treeMaker.Select(treeMaker.Ident(names._super), symbol.name);
        if (symbol.params != null && symbol.params.size() > 0) {
            LinkedHashSet<JCTree.JCExpression> set = new LinkedHashSet<>();
            for (Symbol.VarSymbol param : symbol.params) {
                if (isRessetAnnotations) {
                    param.resetAnnotations();
                }
                set.add(treeMaker.QualIdent(param));
            }
            return callMethod(superExpression, List.from(set));
        } else {
            return callMethod(superExpression, null);
        }
    }

    /*------------------------ execMethod 调用方法并生成可执行语句  ------------------------*/

    /**
     * 执行调用某个类的静态方法
     *
     * @param method 方法的语句
     */
    public JCTree.JCExpressionStatement execMethod(JCTree.JCExpression method) {
        return treeMaker.Exec(method);
    }

    /**
     * 创建方法调用
     *
     * @param typeArgs 参数类型列表
     * @param method   方法的表达式
     * @param args     调用该方法需要传入的参数
     */
    public JCTree.JCExpressionStatement execMethod(List<JCTree.JCExpression> typeArgs, JCTree.JCExpression method,
                                                   List<JCTree.JCExpression> args) {
        return execMethod(callMethod(typeArgs, method, args));
    }

    /**
     * 创建方法调用
     *
     * @param method 方法的表达式
     * @param args   调用该方法需要传入的参数
     */
    public JCTree.JCExpressionStatement execMethod(JCTree.JCExpression method, List<JCTree.JCExpression> args) {
        return execMethod(callMethod(method, args));
    }

    /**
     * 调用某个类或变量的方法
     *
     * @param classPathAndMethodName 方法的语句,必须为:类的全路径.方法名
     *                               例如:java.lang.String.valueOf
     *                               如果上文中有变量 var 则传入 var.indexOf
     * @param args                   调用该方法需要传入的参数
     */
    public JCTree.JCExpressionStatement execMethod(List<JCTree.JCExpression> typeArgs, String classPathAndMethodName,
                                                   List<JCTree.JCExpression> args) {
        return execMethod(callMethod(typeArgs, classPathAndMethodName, args));//参数列表
    }


    /**
     * 调用某个类或变量的方法
     *
     * @param classOrVarName 调用的类的路径,必须为:类的全路径/或者为某个变量的名称
     *                       例如:java.lang.String
     * @param methodName     方法的名称,例如valueOf
     * @param args           调用该方法需要传入的参数
     */
    public JCTree.JCExpressionStatement execMethod(List<JCTree.JCExpression> typeArgs, String classOrVarName,
                                                   String methodName, List<JCTree.JCExpression> args) {
        return execMethod(callMethod(typeArgs, classOrVarName, methodName, args));
    }

    /**
     * 调用某个类或变量的方法
     *
     * @param classOrVarName 调用的类的路径,必须为:类的全路径/或者为某个变量的名称
     *                       例如:java.lang.String
     * @param methodName     方法的名称,例如valueOf
     * @param args           调用该方法需要传入的参数
     */
    public JCTree.JCExpressionStatement execMethod(String classOrVarName, String methodName,
                                                   List<JCTree.JCExpression> args) {
        return execMethod(callMethod(classOrVarName, methodName, args));
    }

    /**
     * 调用某个类或变量的方法
     *
     * @param classOrVarName 调用的类的路径,必须为:类的全路径
     *                       例如:java.lang.String
     * @param methodName     方法的名称,例如valueOf
     * @param args           调用该方法需要传入的参数
     */
    public JCTree.JCExpressionStatement execMethod(String classOrVarName, String methodName, JCTree.JCExpression... args) {
        return execMethod(callMethod(classOrVarName, methodName, args));//参数列表
    }

    /**
     * 调用某个类或变量的方法
     *
     * @param classPathAndMethodName 方法的语句,必须为:类的全路径.方法名
     *                               例如:java.lang.String.valueOf
     *                               如果上文中有变量 var 则传入 var.indexOf
     * @param args                   调用该方法需要传入的参数
     */
    public JCTree.JCExpressionStatement execMethod(String classPathAndMethodName, List<JCTree.JCExpression> args) {
        return execMethod(callMethod(classPathAndMethodName, args));//参数列表
    }


    /**
     * 调用某个类或变量的方法
     *
     * @param classPathAndMethodName 方法的语句,必须为:类的全路径.方法名
     *                               例如:java.lang.String.format
     *                               如果上文中有变量 var 则传入 var.indexOf
     * @param args                   调用该方法需要传入的参数
     */
    public JCTree.JCExpressionStatement execMethod(String classPathAndMethodName, JCTree.JCExpression... args) {
        return execMethod(callMethod(classPathAndMethodName, args));//参数列表
    }


    /**
     * 调用某个类的静态方法
     *
     * @param clazz      调用的类
     * @param methodName 方法的名称,例如valueOf
     * @param args       调用该方法需要传入的参数
     */
    public JCTree.JCExpressionStatement execMethod(Class clazz, String methodName, List<JCTree.JCExpression> args) {
        return execMethod(callMethod(clazz, methodName, args));//参数列表
    }

    /**
     * 调用某个类的静态方法
     *
     * @param clazz      调用的类
     * @param methodName 方法的名称,例如valueOf
     * @param args       调用该方法需要传入的参数
     */
    public JCTree.JCExpressionStatement execMethod(Class clazz, String methodName, JCTree.JCExpression... args) {
        return execMethod(callMethod(clazz, methodName, args));//参数列表
    }


    /**
     * 调用super的方法
     *
     * @param symbol              父类的方法
     * @param isRessetAnnotations 是否需要清理参数中和方法中的注解
     */
    public JCTree.JCExpressionStatement execSuperMethod(Symbol.MethodSymbol symbol, boolean isRessetAnnotations) {
        return execMethod(callSuperMethod(symbol, isRessetAnnotations));
    }

    /*------------------------ createClass 创建类或接口  ------------------------*/

    /**
     * 创建类
     *
     * @param modifiers    访问标志
     * @param className    类名
     * @param typarams     泛型参数列表
     * @param extending    父类
     * @param implementing 实现的接口
     * @param defs         类定义的详细语句，包括字段、方法的定义等等
     * @return
     */
    public JCTree.JCClassDecl createClass(long modifiers, String className, List<JCTree.JCTypeParameter> typarams,
                                          JCTree.JCExpression extending, List<JCTree.JCExpression> implementing,
                                          List<JCTree> defs) {
        JCTree.JCModifiers jcModifiers = treeMaker.Modifiers(modifiers);
        return treeMaker.ClassDef(jcModifiers, getName(className), check(typarams), extending, check(implementing), defs);
    }


    /**
     * 创建类
     *
     * @param className 类的名称
     * @param defs      类定义的详细语句，包括字段、方法的定义等等
     */
    public JCTree.JCClassDecl createClass(int modifiers, String className, List<JCTree> defs) {
        return createClass(modifiers, className, null, null, null, defs);
    }

    /**
     * 创建类
     *
     * @param modifiers 修饰语句
     * @param className 类的名称
     * @param defs      类定义的详细语句，包括字段、方法的定义等等
     */
    public JCTree.JCClassDecl createClass(long modifiers, String className, List<JCTree> defs) {
        return createClass(modifiers, className, null, null, null, defs);
    }


    /**
     * 创建接口
     *
     * @param className
     */
    public JCTree.JCClassDecl createInterface(String className, List<JCTree.JCTypeParameter> typarams,
                                              List<JCTree.JCExpression> implementing, List<JCTree> defs) {
        return createClass(Flags.PUBLIC | Flags.INTERFACE, className, typarams, null, implementing, defs);
    }


    /**
     * 创建接口
     *
     * @param className
     */
    public JCTree.JCClassDecl createInterface(String className, List<JCTree.JCTypeParameter> typarams, List<JCTree> defs) {
        return createClass(Flags.PUBLIC | Flags.INTERFACE, className, typarams, null, null, defs);
    }

    /**
     * 创建内部Interface
     *
     * @param className 类的名称
     * @param defs      类定义的详细语句，包括字段、方法的定义等等
     */
    public JCTree.JCClassDecl createInterface(String className, List<JCTree> defs) {
        return createInterface(className, null, defs);
    }

}
