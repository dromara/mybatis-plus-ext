package com.tangzc.mpe.autotable.annotation.mysql;

import java.lang.annotation.*;

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
