package com.tangzc.mpe.automapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFields {

    /**
     * 全表字段的集合名称，默认是Fields
     */
    String value() default "";
}
