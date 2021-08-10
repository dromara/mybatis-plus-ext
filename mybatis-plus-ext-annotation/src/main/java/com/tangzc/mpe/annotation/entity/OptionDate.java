package com.tangzc.mpe.annotation.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface OptionDate {

    /**
     * 如果字段类型为String，需要制定字符串格式
     */
    String format() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 若对象上存在值，是否覆盖
     */
    boolean override() default true;
}
