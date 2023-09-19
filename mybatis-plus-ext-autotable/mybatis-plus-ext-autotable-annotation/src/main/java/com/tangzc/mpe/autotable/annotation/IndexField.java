package com.tangzc.mpe.autotable.annotation;

import com.tangzc.mpe.autotable.annotation.enums.IndexSortTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 索引字段的详细描述
 * @author don
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface IndexField {

    /**
     * 字段名
     */
    String field();

    /**
     * 字段排序方式
     */
    IndexSortTypeEnum sort();
}
