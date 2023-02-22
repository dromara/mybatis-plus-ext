package com.tangzc.mpe.autotable.annotation;

import com.tangzc.mpe.autotable.annotation.enums.IndexTypeEnum;

import java.lang.annotation.*;

/**
 * 设置字段索引
 * @author don
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Index {

    /**
     * 索引的名字，不设置默认为{mpe_idx_当前标记字段名@Column的name}<p>
     * 如果设置了名字例如union_name,系统会默认在名字前加mpe_idx_前缀，也就是mpe_idx_union_name
     */
    String name() default "";

    /**
     * 索引类型
     */
    IndexTypeEnum type() default IndexTypeEnum.NORMAL;

    /**
     * 索引注释
     */
    String comment() default "";

}

