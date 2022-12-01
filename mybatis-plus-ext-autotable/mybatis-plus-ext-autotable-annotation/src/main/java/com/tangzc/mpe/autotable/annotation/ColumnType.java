package com.tangzc.mpe.autotable.annotation;


import java.lang.annotation.*;


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
     * 字段长度，默认是0
     *
     * @return 字段长度，默认是0
     */
    int length() default 0;

    /**
     * 小数点长度，默认是0
     *
     * @return 小数点长度，默认是0
     */
    int decimalLength() default 0;
}
