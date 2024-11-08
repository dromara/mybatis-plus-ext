package com.tangzc.mpe.condition.metadata;

import java.util.List;

public interface IDynamicConditionHandler {

    /**
     * 匹配结果值，无值表示"is null"，单个值表示"="，多个值表示"in"
     */
    List<Object> values();

    /**
     * 该动态条件是否生效，默认生效，及当没有结果的时候，也会做 where column is null的查询
     */
    default boolean enable() {
        return true;
    }
}
