package com.tangzc.mpe.bind.metadata.annotation;

import com.tangzc.mpe.bind.metadata.enums.AggFuncEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 绑定条件查询的注解
 *
 * @author don
 */
@Inherited
@Documented
@Target({ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AggFuncField {

    /**
     * 聚合函数
     */
    AggFuncEnum func();

    /**
     * 被聚合函数聚合的字段，默认为id，例如：count(1)、sum(1)
     */
    String field() default "1";
}
