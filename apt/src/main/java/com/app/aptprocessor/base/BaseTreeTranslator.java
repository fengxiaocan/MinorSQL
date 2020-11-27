package com.app.aptprocessor.base;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.TreeTranslator;

public class BaseTreeTranslator extends TreeTranslator {
    protected final String fullClassName;
    protected final String simpleClassName;
    protected Symbol.ClassSymbol thisElement;
    protected JCTreeHelper jcHelper;//里面的方法可以创建变量或者类型

    public BaseTreeTranslator(Symbol.ClassSymbol symbol, JCTreeHelper helper) {
        this.thisElement = symbol;
        this.jcHelper = helper;
        this.fullClassName = symbol.getQualifiedName().toString();
        this.simpleClassName = symbol.getSimpleName().toString();
    }

    /**
     * 打印信息
     *
     * @param format
     * @param messages
     */
    protected void printMessage(String format, Object... messages) {
        jcHelper.printMessage(format, messages);
    }

    /**
     * 打印信息
     */
    protected void printMessage(Exception exception) {
        jcHelper.printMessage(exception);
    }

    /**
     * 抛出异常
     *
     * @param message
     */
    protected void throwException(String message, Object... objs) {
        throw new IllegalArgumentException(String.format(message, objs));
    }
}
