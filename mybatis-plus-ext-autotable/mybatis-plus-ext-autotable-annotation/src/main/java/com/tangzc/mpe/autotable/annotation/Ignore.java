package com.tangzc.mpe.autotable.annotation;

import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 标志字段不作为数据库维护的列，同时数据操作的时候也会忽略
 * 具备{@link TableField#exist()}=false的作用
 * @author don
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableField(exist = false)
public @interface Ignore {
}
