package com.tangzc.mpe.autotable.annotation;

import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 字段的默认值
 * 也可通过注解：{@link Column#defaultValueType} {@link Column#defaultValue} 实现
 * @author don
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnDefault {

    /**
     * 字段的默认值
     *
     * @return 字段的默认值
     */
    DefaultValueEnum type() default DefaultValueEnum.UNDEFINED;

    /**
     * 字段的默认值
     *
     * @return 字段的默认值
     */
    String value() default "";
}
