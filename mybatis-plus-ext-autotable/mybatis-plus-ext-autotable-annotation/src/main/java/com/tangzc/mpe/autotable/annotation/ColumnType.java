package com.tangzc.mpe.autotable.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段的类型
 * 也可通过注解：{@link Column#type} {@link Column#length} {@link Column#decimalLength} 实现
 * @author don
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnType {

    /**
     * 字段的类型
     *
     * @return 字段的类型
     */
    String value() default "";

    /**
     * 字段长度，默认是-1，小于0相当于null
     */
    int length() default -1;

    /**
     * 小数点长度，默认是-1，小于0相当于null
     */
    int decimalLength() default -1;
}
