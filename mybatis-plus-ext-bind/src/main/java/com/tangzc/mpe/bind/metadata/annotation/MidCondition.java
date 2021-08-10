package com.tangzc.mpe.bind.metadata.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Inherited
@Documented
@Target({ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MidCondition {

    /**
     * 中间表Entity，需要对应创建其Mapper
     */
    Class<?> midEntity();

    /**
     * 关联Entity所需的自身字段
     */
    String selfField() default "id";

    /**
     * 关联Entity所需的自身字段，中间表字段名
     */
    String selfMidField();

    /**
     * 被关联Entity的关联字段
     */
    String joinField() default "id";

    /**
     * 被关联Entity的关联字段，中间表字段名
     */
    String joinMidField();
}
