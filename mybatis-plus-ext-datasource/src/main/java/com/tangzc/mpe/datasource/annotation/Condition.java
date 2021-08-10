package com.tangzc.mpe.datasource.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据来源关联的条件
 * @author don
 */
@Inherited
@Documented
@Target({ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Condition {

    /**
     * 关联数据来源Entity所需的自身字段
     */
    String selfField();

    /**
     * 数据来源的Entity的字段，默认为id
     */
    String sourceField() default "id";
}
