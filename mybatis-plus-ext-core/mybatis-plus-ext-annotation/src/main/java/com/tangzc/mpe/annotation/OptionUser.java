package com.tangzc.mpe.annotation;


import com.tangzc.mpe.annotation.handler.AutoFillHandler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动填充自定义值，通常用不到，直接使用衍生的注解 {@link InsertOptionUser} 或者 {@link UpdateOptionUser} 或者 {@link InsertUpdateOptionUser}
 * @author don
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface OptionUser {

    /**
     * 可以自定义用户信息生成方式
     */
    Class<? extends AutoFillHandler> value();

    /**
     * 若对象上存在值，是否覆盖
     */
    boolean override() default true;
}
