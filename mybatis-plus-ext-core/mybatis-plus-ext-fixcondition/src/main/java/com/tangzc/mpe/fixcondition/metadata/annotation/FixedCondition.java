package com.tangzc.mpe.fixcondition.metadata.annotation;

import com.tangzc.mpe.annotation.DefaultValue;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 固定查询条件
 */
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@DefaultValue("")
public @interface FixedCondition {

    /**
     * 固定值
     */
    @AliasFor(annotation = DefaultValue.class, attribute = "value")
    String value();
}
