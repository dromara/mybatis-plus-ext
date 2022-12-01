package com.tangzc.mpe.autotable.annotation;

import com.tangzc.mpe.autotable.annotation.enums.IndexSortTypeEnum;

/**
 * 索引字段的详细描述
 * @author don
 */
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
