package com.tangzc.mpe.datasource.annotation;

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
    Class<?> source() default Void.class;
    /**
     * 数据来源的Entity class 的全路径名称（包名.类名）
     */
    String sourceName() default "";

    /**
     * 数据来源的Entity对应的属性
     */
    String field();

    /**
     * 数据来源
     */
    Condition[] conditions();

    /**
     * 执行更新的时候额外的where条件
     * 通常指被更新表自身的特殊条件，例如：enable=1 and is_deleted=0
     */
    String updateCondition() default "";
}
