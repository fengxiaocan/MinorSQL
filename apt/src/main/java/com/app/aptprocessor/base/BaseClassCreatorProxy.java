package com.app.aptprocessor.base;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Field;
import java.util.HashSet;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * 类的生成器基类
 */
public abstract class BaseClassCreatorProxy {
    protected String mBindingClassName;//生成的绑定类
    protected String mPackageName;//包名
    protected TypeElement mTypeElement;//依赖的类
    protected ClassName mHostClassName;//依赖的类的名
    protected ClassName mBindingClass;//生成的类的名

    public BaseClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        this.mTypeElement = classElement;
        //获取依赖的类的包名
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();

        this.mPackageName = packageName;
        //获取依赖的类的名称
        String className = mTypeElement.getSimpleName().toString();
        this.mBindingClassName = createBindingClassName(className);
        mHostClassName = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        mBindingClass = ClassName.get(mPackageName, mBindingClassName);
    }

    /**
     * 生成的类的文件名
     *
     * @param className
     * @return
     */
    protected abstract String createBindingClassName(String className);

    /**
     * 获取包名
     *
     * @return
     */
    public String getPackageName() {
        return mPackageName;
    }

    /**
     * 获取绑定的类的名
     *
     * @return
     */
    public String getBindingClassName() {
        return mBindingClassName;
    }

    /**
     * 生成成员变量
     *
     * @return
     */
    protected Iterable<FieldSpec> generateFields(Field[] fields, Modifier... modifiers) {
        HashSet<FieldSpec> fieldSpecs = new HashSet();
        for (Field field : fields) {
            fieldSpecs.add(FieldSpec
                    .builder(ClassName.get(field.getType()), field.getName(), modifiers)
                    .build());
        }
        return fieldSpecs;
    }


    /**
     * 生成私有的成员变量
     *
     * @param fields
     * @return
     */
    protected Iterable<FieldSpec> generatePrivateFields(Field[] fields) {
        return generateFields(fields, Modifier.PRIVATE);
    }

    /**
     * 转换为成员变量
     *
     * @param element
     * @param modifiers 修饰属性
     * @return
     */
    protected FieldSpec toField(VariableElement element, Modifier... modifiers) {
        String name = element.getSimpleName().toString();
        TypeMirror typeMirror = element.asType();
        return FieldSpec.builder(ClassName.get(typeMirror), name, modifiers).build();
    }

    /**
     * 转换为成员变量
     *
     * @param element
     * @return
     */
    protected FieldSpec toPrivateField(VariableElement element) {
        return toField(element, Modifier.PRIVATE);
    }

    /**
     * 构建一个成员方法的Builder,返回值为本身
     *
     * @param modifiers
     * @return
     */
    protected MethodSpec.Builder setMethodBuilder(String name, TypeName returnType, Modifier... modifiers) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
                .returns(mBindingClass)//返回类型
                .addModifiers(modifiers)
                .addParameter(returnType, name);//参数类型、名字
        //直接添加代码
        methodBuilder.addCode(String.format("this.%s = %s;", name, name));
        methodBuilder.addCode("\n");
        methodBuilder.addCode("return this;\n");
        return methodBuilder;
    }

    /**
     * 构建一个没有返回值的Setter方法
     *
     * @param modifiers
     * @return
     */
    protected MethodSpec.Builder setMethod(String name, TypeName returnType, Modifier... modifiers) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
                .addModifiers(modifiers)
                .returns(TypeName.VOID)
                .addParameter(returnType, name);
        methodBuilder.addCode(String.format("this.%s = %s;", name, name));
        methodBuilder.addCode("\n");
        return methodBuilder;
    }

    /**
     * 构建一个Builder Setter方法,返回值为本身
     *
     * @param modifiers
     * @return
     */
    protected MethodSpec.Builder setterMethodBuilder(String name, TypeName returnType, Modifier... modifiers) {
        String specName;
        if (name.length() == 1) {
            specName = name.toUpperCase();
        } else {
            specName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(specName)
                .addModifiers(modifiers)
                .returns(mBindingClass)
                .addParameter(returnType, name);
        methodBuilder.addCode(String.format("this.%s = %s;", name, name));
        methodBuilder.addCode("\n");
        methodBuilder.addCode("return this;\n");
        return methodBuilder;
    }

    /**
     * 构建一个没有返回值的Setter方法
     *
     * @param modifiers
     * @return
     */
    protected MethodSpec.Builder setterMethod(String name, TypeName returnType, Modifier... modifiers) {
        String specName;
        if (name.length() == 1) {
            specName = name.toUpperCase();
        } else {
            specName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(specName)
                .addModifiers(modifiers)
                .returns(mBindingClass)
                .addParameter(returnType, name);

        methodBuilder.addCode(String.format("this.%s = %s;", name, name));
        methodBuilder.addCode("\n");
        return methodBuilder;
    }

    /**
     * 构建Getter  区别在于加上get前缀+驼峰命名
     *
     * @param modifiers
     * @param name
     * @param returnType
     * @return
     */
    protected MethodSpec.Builder getterMethod(String name, TypeName returnType, Modifier... modifiers) {
        String specName;
        if (name.length() == 1) {
            specName = name.toUpperCase();
        } else {
            specName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return MethodSpec.methodBuilder(specName)
                .addModifiers(modifiers)
                .returns(returnType)
                .addCode(String.format("return %s;", name));
    }

    /**
     * 构建Getter 区别在于直接以名字做方法名
     *
     * @param modifiers
     * @param name
     * @param returnType
     * @return
     */
    protected MethodSpec.Builder buildGetMethod(String name, TypeName returnType, Modifier... modifiers) {
        return MethodSpec.methodBuilder(name)
                .addModifiers(modifiers)
                .returns(returnType)
                .addCode(String.format("return %s;", name));
    }

}
