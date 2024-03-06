package com.tangzc.mpe.processer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoDefine {

    /**
     * 包含实体所有字段的类的完整名称
     */
    String value() default "";

    /**
     * 包含实体所有字段的类的名称后缀
     */
    String suffix() default "";

    /**
     * 包含实体所有字段的类的包名，默认与Entity包名一致
     */
    String packageName() default "";
}
