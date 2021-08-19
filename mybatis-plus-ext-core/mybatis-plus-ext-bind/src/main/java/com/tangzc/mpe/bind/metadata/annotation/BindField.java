package com.tangzc.mpe.bind.metadata.annotation;

import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 绑定字段 （one to one）（one to many）
 *
 * @author don
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@TableField(exist = false)
@Inherited
@Documented
public @interface BindField {

    /**
     * 被关联的Entity
     */
    Class<?> entity();

    /***
     * 被关联的Entity的具体字段
     */
    String field();

    /**
     * 关联Entity所需要的条件
     */
    JoinCondition[] conditions();

    /**
     * 被关联的Entity所需要的额外条件
     * 通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0
     */
    String customCondition() default "";

    /**
     * 被关联的Entity的结果集，排序条件
     */
    JoinOrderBy[] orderBy() default {};
}