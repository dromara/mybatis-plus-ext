package com.tangzc.mpe.autotable.annotation;

import com.tangzc.mpe.autotable.annotation.enums.IndexTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 设置字段唯一索引 {@link Index}的快捷方式
 *
 * @author don
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Index(type = IndexTypeEnum.UNIQUE)
public @interface UniqueIndex {

    /**
     * 索引的名字，不设置默认为{mpe_idx_当前标记字段名@Column的name}<p>
     * 如果设置了名字例如union_name,系统会默认在名字前加mpe_idx_前缀，也就是mpe_idx_union_name
     */
    @AliasFor(annotation = Index.class, attribute = "name")
    String name() default "";

    /**
     * 索引注释
     */
    @AliasFor(annotation = Index.class, attribute = "comment")
    String comment() default "";

}

