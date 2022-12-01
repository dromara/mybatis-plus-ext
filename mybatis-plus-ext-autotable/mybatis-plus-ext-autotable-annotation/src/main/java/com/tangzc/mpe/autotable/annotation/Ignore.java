package com.tangzc.mpe.autotable.annotation;

import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.annotation.*;


/**
 * 标志字段不作为数据库维护的列
 * @author don
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableField(exist = false)
public @interface Ignore {
}
