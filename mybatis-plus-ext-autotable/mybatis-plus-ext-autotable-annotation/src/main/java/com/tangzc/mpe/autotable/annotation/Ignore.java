package com.tangzc.mpe.autotable.annotation;

import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.annotation.*;


/**
 * 标志字段不作为数据库维护的列，同时数据操作的时候也会忽略
 * 区别于 {@link Table#excludeFields()}, {@link Table#excludeFields()}仅会忽略建表
 *
 * @author don
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableField(exist = false)
public @interface Ignore {
}
