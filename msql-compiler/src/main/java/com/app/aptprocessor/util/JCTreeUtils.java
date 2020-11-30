package com.app.aptprocessor.util;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class JCTreeUtils {
    public static<T> List<T> check(List<T> list){
        if (list == null){
            list = List.nil();
        }
        return list;
    }

    public static ListBuffer<JCTree.JCStatement> buffer() {
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer();
        return jcStatements;
    }

    public static boolean isEquals(Name name, String parName) {
        return parName.equals(name.toString());
    }

    public static boolean isEquals(JCTree.JCVariableDecl name, String parName) {
        return parName.equals(name.name.toString());
    }

    /**
     * 是否是同一个方法
     *
     * @param methodDecl 方法节点树
     * @param methodName 方法名称
     * @param params     参数的类的简写名称,注意不是全类名
     * @return
     */
    public static boolean isSameMethod(JCTree.JCMethodDecl methodDecl, String methodName, String... params) {
        if (methodDecl.name.toString().equals(methodName)) {
            int methodSize = methodDecl.params == null ? 0 : methodDecl.params.size();
            int paramsSize = params == null ? 0 : params.length;
            if (methodSize == 0 && paramsSize == 0) {
                return true;
            } else if (methodSize == paramsSize) {
                for (int i = 0; i < methodDecl.params.size(); i++) {
                    JCTree.JCVariableDecl decl = methodDecl.params.get(i);
                    if (!params[i].equals(decl.vartype.toString())) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }

        }
        return false;
    }

    /**
     * 是否是相同的方法
     *
     * @param methodDecl 方法节点树
     * @param methodName 方法名
     * @param params     参数的全类名
     * @return
     */
    public static boolean isSameMethod(Symbol.MethodSymbol methodDecl, String methodName, String... params) {
        if (methodDecl.name.toString().equals(methodName)) {
            int methodSize = methodDecl.params == null ? 0 : methodDecl.params.size();
            int paramsSize = params == null ? 0 : params.length;
            if (methodSize == 0 && paramsSize == 0) {
                return true;
            } else if (methodSize == paramsSize) {
                for (int i = 0; i < methodDecl.params.size(); i++) {
                    Symbol.VarSymbol varSymbol = methodDecl.params.get(i);
                    if (!params[i].equals(varSymbol.asType().toString())) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }

        }
        return false;
    }

    /**
     * 是否是相同的方法
     *
     * @param methodDecl 方法节点树
     * @param methodName 方法名
     * @param paramsSize 参数的数量
     * @return
     */
    public static boolean isSameMethod(Symbol.MethodSymbol methodDecl, String methodName, int paramsSize) {
        if (methodDecl.name.toString().equals(methodName)) {
            int methodSize = methodDecl.params == null ? 0 : methodDecl.params.size();
            return methodSize == paramsSize;

        }
        return false;
    }


    public static boolean isActivity(TypeElement classElement) {
        return isInstanceOf(classElement, "android.app.Activity");
    }

    public static boolean isView(TypeElement classElement) {
        return isInstanceOf(classElement, "android.view.View");
    }

    public static boolean isFragment(TypeElement classElement) {
        return isInstanceOf(classElement, "android.app.Fragment") ||
                isInstanceOf(classElement, "androidx.fragment.app.Fragment");
    }

    public static Symbol.ClassSymbol getSuperTypeElement(Type.ClassType classType,
                                                         String... superClass) {
        if (classType != null && classType.tsym != null &&
                classType.tsym instanceof Symbol.ClassSymbol) {
            Symbol.ClassSymbol typeSymbol = (Symbol.ClassSymbol) classType.asElement();

            for (String aClass : superClass) {
                if (typeSymbol.toString().equals(aClass)) {
                    return typeSymbol;
                }
            }

            Type superclass = typeSymbol.getSuperclass();
            if (superclass instanceof Type.ClassType) {
                return getSuperTypeElement(((Type.ClassType) superclass), superClass);
            }
        }
        return null;
    }

    /**
     * 判断是否继承某个类
     *
     * @param element
     * @param className
     * @return
     */
    public static boolean isInstanceOf(TypeElement element, String className) {
        if (element.getQualifiedName().toString().equals(className)) {
            return true;
        }
        TypeMirror parent = element.getSuperclass();
        return isInstanceOf(parent, className);
    }

    /**
     * 是否继承某个类
     *
     * @param element
     * @param className
     * @return
     */
    public static boolean isInstanceOf(TypeMirror element, String className) {
        if (element.toString().equals(className)) {
            return true;
        }
        if (element instanceof DeclaredType) {
            Element elt = ((DeclaredType) element).asElement();
            if (elt instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) elt;
                return isInstanceOf(typeElement, className);
            }
        }
        return false;
    }

    /**
     * 是否实现某个接口
     *
     * @param element
     * @param className
     * @return
     */
    public static boolean isInterfacesOf(VariableElement element, String className) {
        if (element.asType() instanceof Type.ClassType) {
            Type.ClassType classType = (Type.ClassType) element.asType();
            return isInterfacesOf(classType, className);
        }
        return false;
    }

    /**
     * 是否实现某个接口
     *
     * @param classType
     * @param className
     * @return
     */
    public static boolean isInterfacesOf(Type.ClassType classType, String className) {
        List<Type> interfaces_field = classType.interfaces_field;
        if (interfaces_field != null) {
            for (Type type : interfaces_field) {
                if (type.tsym.toString().equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 能够判断是否java.util.ArrayList<com.app.apptest.ParcelableBean>数组类型
     *
     * @param element
     * @param className
     * @return
     */
    public static boolean isInterfacesOfList(VariableElement element, String className) {
        if (element.asType() instanceof Type.ClassType) {
            Type.ClassType classType = (Type.ClassType) element.asType();
            return isInterfacesOfList(classType, className);
        }
        return false;
    }

    /**
     * 能够判断是否 java.util.ArrayList<com.app.apptest.ParcelableBean> 数组类型
     *
     * @param classType
     * @param className
     * @return
     */
    public static boolean isInterfacesOfList(Type.ClassType classType, String className) {
        List<Type> types = classType.getTypeArguments();
        if (types != null) {
            for (Type type : types) {
                if (type instanceof Type.ClassType) {
                    if (isInterfacesOf((Type.ClassType) type, className)) {
                        return true;
                    }
                } else if (type instanceof Type.ArrayType) {
                    if (isInterfacesOfArray((Type.ArrayType) type, className)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 数组的某个类是否实现某个接口
     *
     * @param element
     * @param className
     * @return
     */
    public static boolean isInterfacesOfArray(VariableElement element, String className) {
        if (element.asType() instanceof Type.ArrayType) {
            return isInterfacesOfArray((Type.ArrayType) element.asType(), className);
        }
        return false;
    }

    /**
     * 数组的某个类是否实现某个接口
     *
     * @param arrayType
     * @param className
     * @return
     */
    public static boolean isInterfacesOfArray(Type.ArrayType arrayType, String className) {
        Type elemtype = arrayType.elemtype;
        if (elemtype instanceof Type.ClassType) {
            List<Type> interfaces_field = ((Type.ClassType) elemtype).interfaces_field;
            for (Type type : interfaces_field) {
                if (type.toString().equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否实现某个接口或者是否是某个类的子类
     *
     * @param classType
     * @param interfaceClass
     * @return
     */
    public static boolean isInstanceOf(Class<?> classType, Class<?> interfaceClass) {
        return interfaceClass.isAssignableFrom(classType);
    }

    /**
     * 是否是基本数据类型
     *
     * @param type 成员变量的表达
     * @return
     */
    public static boolean isPrimitiveType(JCTree.JCExpression type) {
        return type instanceof JCTree.JCPrimitiveTypeTree;
    }

    /**
     * 是否是基本数据类型
     *
     * @param decl 获取到的成员变量
     * @return
     */
    public static boolean isPrimitiveType(JCTree.JCVariableDecl decl) {
        return isPrimitiveType(decl.vartype);
    }

    /**
     * 是否是基本数据类型
     *
     * @param decl 获取到的成员变量
     * @return
     */
    public static boolean isPrimitiveType2(JCTree.JCVariableDecl decl) {
        String type = getVariableType(decl);
        switch (type) {
            case "boolean":
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
            case "char":
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否是数组类型
     *
     * @param type
     * @return
     */
    public static boolean isArrayType(JCTree.JCExpression type) {
        return type instanceof JCTree.JCPrimitiveTypeTree;
    }

    /**
     * 是否是数组类型
     *
     * @param decl
     * @return
     */
    public static boolean isArrayType(JCTree.JCVariableDecl decl) {
        return isArrayType(decl.vartype);
    }

    /**
     * 是否是基本数据类型的数组类型
     *
     * @param type
     * @return
     */
    public static boolean isPrimitiveTypeArray(JCTree.JCExpression type) {
        if (isArrayType(type)) {
            String ty = type.type.toString();
            return isStartsWithOf(ty,
                    "boolean[]", "byte[]", "short[]", "int[]", "long[]",
                    "float[]", "double[]", "char[]",
                    "java.lang.Boolean[]", "java.lang.Byte[]",
                    "java.lang.Short[]", "java.lang.Integer[]",
                    "java.lang.Long[]", "java.lang.Float[]",
                    "java.lang.Double[]", "java.lang.Character[]",
                    "java.lang.String[]");
        }
        return false;
    }

    /**
     * 是否是基本数据类型的数组类型
     *
     * @param decl
     * @return
     */
    public static boolean isPrimitiveTypeArray(JCTree.JCVariableDecl decl) {
        return isPrimitiveTypeArray(decl.vartype);
    }

    /**
     * 是否是基本数据类型(包括Byte等包装类型)或者String类型
     *
     * @param dec 成员变量
     * @return
     */
    public static boolean isBasicType(JCTree.JCVariableDecl dec) {
        if (isPrimitiveType(dec) && isPrimitiveType2(dec)) {
            return true;
        }
        if (dec.vartype instanceof JCTree.JCIdent) {
            return isEqualsOf(dec.vartype.type.toString(),
                    "java.lang.Boolean", "java.lang.Byte", "java.lang.Short",
                    "java.lang.Integer", "java.lang.Long", "java.lang.Float",
                    "java.lang.Double", "java.lang.Character", "java.lang.String");
        }
        return false;
    }

    /**
     * 判断变量是否有注解
     *
     * @param dec
     * @return
     */
    public static boolean isHasAnnotation(JCTree.JCVariableDecl dec) {
        return dec.mods.annotations != null && dec.mods.annotations.size() > 0;
    }

    /**
     * 是否是自身的成员变量
     *
     * @param mySelfSymbol 自身的表达式
     * @param decl         获取到的成员变量
     * @return
     */
    public static boolean isOwnVariable(Symbol mySelfSymbol, JCTree.JCVariableDecl decl) {
        //对比成员变量表达对象的拥有者
        if (mySelfSymbol == null) {
            return false;
        }
        if (decl == null || decl.sym == null || decl.sym.owner == null) {
            return false;
        }
        return mySelfSymbol.getQualifiedName().toString().equals(decl.sym.owner.toString());
    }

    /**
     * 检测 修饰符
     *
     * @param dec
     * @return
     */
    public static boolean checkModifier(JCTree.JCVariableDecl dec) {
        Set<Modifier> flags = dec.getModifiers().getFlags();
        if (flags == null || flags.size() == 0) {
            return true;
        } else {
            return !(flags.contains(Modifier.STATIC) || flags.contains(Modifier.FINAL)
                    || flags.contains(Modifier.ABSTRACT) || flags.contains(Modifier.NATIVE));
        }
    }

    /**
     * 获取参数的类型
     *
     * @param dec
     * @return
     */
    public static String getVariableType(JCTree.JCVariableDecl dec) {
        return dec.vartype.type.toString();
    }

    public static boolean isStartsWithOf(String content, String... check) {
        for (String str : check) {
            if (content.startsWith(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEqualsOf(String content, String... check) {
        for (String str : check) {
            if (content.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static String join(String ... joins){
        StringBuilder sb = new StringBuilder();
        for (String join : joins) {
            sb.append(join);
        }
        return sb.toString();
    }
}
