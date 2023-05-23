package com.tangzc.mpe.autotable.annotation.mysql;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定MySQL引擎
 * @author don
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MysqlEngine {

    /**
     * 引擎名称
     */
    String value();
}
