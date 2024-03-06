package com.tangzc.mpe.automapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRepository {

    /**
     * 完整的Mapper名称，指定后，将会替换掉默认生成策略
     */
    String value() default "";

    /**
     * 生成的Repository类的后缀，前半部分固定为Entity的名字
     */
    String suffix() default "";

    /**
     * 生成Repository所在的包名，默认与Entity包名一致
     */
    String packageName() default "";

    /**
     * 自动创建Mapper
     */
    AutoMapper autoMapper() default @AutoMapper;
}
