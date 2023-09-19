package com.tangzc.mpe.autotable.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 字段的备注
 * 也可通过注解：{@link Column#comment} 实现
 * @author don
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnComment {

    /**
     * 字段备注
     *
     * @return 字段备注
     */
    String value();
}
