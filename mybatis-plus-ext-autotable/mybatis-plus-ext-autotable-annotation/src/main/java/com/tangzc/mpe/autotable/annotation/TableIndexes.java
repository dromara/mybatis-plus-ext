package com.tangzc.mpe.autotable.annotation;

import java.lang.annotation.*;

/**
 * 设置多个表索引
 * @author tangzc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableIndexes {

    /**
     * 索引集合
     */
    TableIndex[] value();

}

