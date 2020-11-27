package com.app.aptprocessor.processor;

import com.app.annotation.SQLite;
import com.app.aptprocessor.base.BaseProcessor;
import com.google.auto.service.AutoService;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/**
 * 对View进行操作的Processor
 */
@AutoService(Processor.class)
public class SQLiteProcessor extends BaseProcessor {
    public static Set<String> TABLE_NAMES = new HashSet<>();//保存

    @Override
    protected Class[] annotationTypes() {
        return new Class[]{SQLite.class};
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver()) {
            TABLE_NAMES.clear();
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(SQLite.class);
            for (Element element : elements) {
                if (element.getKind() == ElementKind.CLASS) {
                    JCTree tree = (JCTree) jcTreeHelper.trees.getTree(element);
                    Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) element;
                    SQLiteTreeTranslator translator = new SQLiteTreeTranslator(classSymbol,jcTreeHelper);
                    tree.accept(translator);
                    SQLInterfaceJavaCode javaCode = translator.getJavaCode();
                    javaFile(javaCode.getPackageName(), javaCode.generateJavaCode());
                }
            }
            TABLE_NAMES.clear();
        }
        return false;
    }

}
