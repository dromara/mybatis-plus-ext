package com.tangzc.mybatis.relevance.metadata.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 绑定条件查询的注解
 * @author don
 */
@Inherited
@Documented
@Target({ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinCondition {

    /**
     * 关联Entity所需的自身字段
     */
    String selfField();

    /**
     * 被关联Entity的关联字段，默认为关联Entity的id
     */
    String joinField() default "id";
}
