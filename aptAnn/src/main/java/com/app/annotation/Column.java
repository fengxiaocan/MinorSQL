package com.app.annotation;

public @interface Column {

    String name() default "";//设置的名字

    boolean id() default false;//是否是id

    boolean nullable() default true;//是否可以为null

    boolean unique() default false;//独一无二的

    String defaultValue() default "";

    boolean ignore() default false;
}
