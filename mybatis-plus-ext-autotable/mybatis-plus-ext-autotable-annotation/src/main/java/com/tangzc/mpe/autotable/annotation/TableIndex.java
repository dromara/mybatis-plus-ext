package com.tangzc.mpe.autotable.annotation;

import com.tangzc.mpe.autotable.annotation.enums.IndexTypeEnum;

import java.lang.annotation.*;

/**
 * 设置表索引
 * @author tangzc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableIndex {

    /**
     * <p>索引的名字，设置了名字例如union_name,系统会默认在名字前加mpe_idx_前缀，也就是mpe_idx_union_name
     */
    String name();

    /**
     * 索引类型
     */
    IndexTypeEnum type() default IndexTypeEnum.NORMAL;

    /**
     * <p>字段名：支持多字段
     * <p>注意，多字段的情况下，字段书序即构建索引时候的顺序，牵扯索引左匹配问题
     * <p>该配置优先级低于{@link #indexFields()}，具体可参考{@link #indexFields()}的说明
     */
    String[] fields();

    /**
     * <p>字段名：兼容需要指定字段排序方式的模式
     * <p>注意，多字段的情况下，字段书序即构建索引时候的顺序，牵扯索引左匹配问题
     * <p>该配置优先级高于{@link #fields()}，也就是说，生成索引字段的顺序，该配置中的列会排在{@link #fields()}之前，同时，如果该配置与{@link #fields()}之间存在重名的情况，以该配置为主
     */
    IndexField[] indexFields() default {};

    /**
     * 索引注释: 默认空字符串
     */
    String comment() default "";

}

