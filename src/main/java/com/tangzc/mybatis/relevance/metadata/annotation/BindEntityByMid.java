package com.tangzc.mybatis.relevance.metadata.annotation;

import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 绑定Entity （one to one）（one to many）
 *
 * @author don
 */
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@TableField(exist = false)
public @interface BindEntityByMid {

    /**
     * 被关联的Entity，不再需要显示的指明，默认取字段上的声明类型
     */
    Class<?> entity() default Void.class;

    /**
     * 中间表关联条件
     */
    MidCondition condition();

    /**
     * 被关联的Entity所需要的额外条件
     * 通常指被关联的Entity自身的特殊条件，例如：enable=1 and is_deleted=0
     */
    String customCondition() default "";

    /**
     * 被关联的Entity的结果集，排序条件
     */
    JoinOrderBy[] orderBy() default {};

    /**
     * 深度绑定（不熟悉的，不建议使用）
     */
    boolean deepBind() default false;
}