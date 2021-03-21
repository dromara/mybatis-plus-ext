package com.tangzc.mybatis.relevance.metadata.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Inherited
@Documented
@Target({ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinOrderBy {

    /**
     * 被关联的Entity中结果集排序字段
     */
    String field();

    /**
     * 排序，true:正序，false:倒序
     */
    boolean isAsc() default true;
}
