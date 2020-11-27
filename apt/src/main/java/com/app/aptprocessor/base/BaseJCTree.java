package com.app.aptprocessor.base;

import com.app.aptprocessor.util.JCTreeUtils;
import com.squareup.javapoet.ClassName;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

public class BaseJCTree extends JCTreeUtils {
    // javac 编译器相关类
    public Trees trees;//提供了待处理的抽象语法树
    public Filer filer;
    public Elements mElementUtils;
    public Messager messager;

    public TreeMaker treeMaker;//创建class字节码的工具,封装了创建AST节点的一些方法
    public Names names;//创建name类型的工具类,提供了创建标识符的方法
    public Symtab symtab;//保存有 部分 Type类型
    public Types types;
    public ClassReader classReader;


    public BaseJCTree(ProcessingEnvironment environment) {
        this.filer = environment.getFiler();
        this.messager = environment.getMessager();
        this.mElementUtils = environment.getElementUtils();
        this.trees = Trees.instance(environment);
        Context context = ((JavacProcessingEnvironment) environment).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.symtab = Symtab.instance(context);
        this.names = Names.instance(context);
        this.types = Types.instance(context);
        this.classReader = ClassReader.instance(context);
    }

    /*基本数据类型*/
    public Type.JCPrimitiveType byteType() {
        return symtab.byteType;
    }

    public Type.JCPrimitiveType charType() {
        return symtab.charType;
    }

    public Type.JCPrimitiveType shortType() {
        return symtab.shortType;
    }

    public Type.JCPrimitiveType intType() {
        return symtab.intType;
    }

    public Type.JCPrimitiveType longType() {
        return symtab.longType;
    }

    public Type.JCPrimitiveType floatType() {
        return symtab.floatType;
    }

    public Type.JCPrimitiveType doubleType() {
        return symtab.doubleType;
    }

    public Type.JCPrimitiveType booleanType() {
        return symtab.booleanType;
    }

    /*基本数据类型*/

    public Type stringType() {
        return symtab.stringType;
    }

    /**
     * 创建 String[] 字符串数组 Type
     *
     * @return
     */
    public Type.ArrayType stringArrayType() {
        return arrayType(stringType());
    }

    /**
     * 创建 String... name 字符串可变数组 Type
     *
     * @return
     */
    public Type.ArrayType stringVarargsArrayType() {
        return varargsArrayType(stringType());
    }

    /**
     * 创建 对象数组 Type
     *
     * @return
     */
    public Type.ArrayType arrayType(Type type) {
        return new Type.ArrayType(type, symtab.arrayClass);
    }

    /**
     * 创建 对象可变数组 Type
     *
     * @return
     */
    public Type.ArrayType varargsArrayType(Type type) {
        return arrayType(type).makeVarargs();
    }

    /**
     * 根据字符串获取Name，（利用Names的fromString静态方法）
     *
     * @param className
     * @return
     */
    public Name getName(String className) {
        return names.fromString(className);
    }

    /**
     * 根据Name创建ClassSymbol类的元素
     */
    public Symbol.ClassSymbol makeClassSymbol(Name name) {
        return classReader.enterClass(name);
    }

    /**
     * 根据class的路径创建ClassSymbol类的元素
     */
    public Symbol.ClassSymbol makeClassSymbol(String classPath) {
        return makeClassSymbol(getName(classPath));
    }

    /**
     * 根据Name创建PackageSymbol包的元素
     */
    public Symbol.PackageSymbol makePackageSymbol(Name name) {
        return classReader.enterPackage(name);
    }

    /**
     * 根据包名创建PackageSymbol包的元素
     */
    public Symbol.PackageSymbol makePackageSymbol(String packagePath) {
        return makePackageSymbol(getName(packagePath));
    }

    /**
     * 创建变量元素
     *
     * @param modifiers 变量的修饰符
     * @param var3      变量的名称
     * @param clazz     变量的类型的元素
     */
    public Symbol.VarSymbol makeVarSymbol(long modifiers, Name var3, Symbol.ClassSymbol clazz) {
        return new Symbol.VarSymbol(modifiers, var3, clazz.type, clazz);
    }

    /**
     * 根据类的路径创建变量元素
     *
     * @param modifiers 变量的修饰符
     * @param var3      变量的名称
     * @param classPath 变量的类型的路径
     */
    public Symbol.VarSymbol makeVarSymbol(long modifiers, Name var3, String classPath) {
        return makeVarSymbol(modifiers, var3, makeClassSymbol(classPath));
    }

    /**
     * 根据类的路径创建变量元素
     *
     * @param modifiers 变量的修饰符
     * @param VarName   变量的名称
     * @param classPath 变量的类型的路径
     */
    public Symbol.VarSymbol makeVarSymbol(long modifiers, String VarName, String classPath) {
        return makeVarSymbol(modifiers, getName(VarName), classPath);
    }

    /**
     * 根据类的路径创建变量元素
     *
     * @param modifiers 变量的修饰符
     * @param VarName   变量的名称
     * @param clazz     变量的类型
     */
    public Symbol.VarSymbol makeVarSymbol(long modifiers, String VarName, Class clazz) {
        return makeVarSymbol(modifiers, getName(VarName), clazz.getName());
    }

    /**
     * 根据类的路径创建变量元素
     *
     * @param VarName   变量的名称
     * @param classPath 变量的类型的路径
     */
    public Symbol.VarSymbol makeVarSymbol(String VarName, String classPath) {
        return makeVarSymbol(0, VarName, classPath);
    }

    /**
     * 创建类的类型
     *
     * @param name 名称
     */
    public Type makeClassType(Name name) {
        return makeClassSymbol(name).type;
    }

    /**
     * 创建类的类型
     *
     * @param classPath 类的全路径
     */
    public Type makeClassType(String classPath) {
        return makeClassSymbol(classPath).type;
    }


    /**
     * 获取ClassName
     */
    public ClassName getClassName(String packageName, String simpleName, String... simpleNames) {
        return ClassName.get(packageName, simpleName, simpleNames);
    }

    /**
     * 打印信息
     *
     * @param format
     * @param messages
     */
    protected void printMessage(String format, Object... messages) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(format, messages));
    }

    /**
     * 打印信息
     */
    protected void printMessage(Exception exception) {
        printMessage("Exception:%s", exception.getClass().getName());
        if (exception.getMessage() != null) {
            printMessage("          %s", exception.getMessage());
        }
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            printMessage("          %s : %s", element.getClassName(), element.getMethodName());
        }
    }

    /**
     * 获取包名
     */
    protected String getPackageName(Element element) {
        String string = mElementUtils.getPackageOf(element).asType().toString();
        String[] split = string.split("\\.");
        if (split.length > 3) {
            return split[0] + "." + split[1] + "." + split[2];
        } else {
            return string;
        }
    }
}
