package com.tangzc.mybatis.datasource.metadata.annotation;

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
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {

    /**
     * 数据来源的Entity class
     */
    Class<?> source();

    /**
     * 数据来源的Entity对应的属性
     */
    String field();

    /**
     * 数据来源
     */
    Condition[] condition();
}
