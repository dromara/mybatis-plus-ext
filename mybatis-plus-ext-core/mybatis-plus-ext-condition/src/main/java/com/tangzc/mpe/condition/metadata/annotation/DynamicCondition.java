package com.tangzc.mpe.condition.metadata.annotation;

import com.tangzc.mpe.condition.metadata.IDynamicConditionHandler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态查询条件
 * @author don
 */
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicCondition {

    /**
     * 动态值
     */
    Class<? extends IDynamicConditionHandler> value();
}
