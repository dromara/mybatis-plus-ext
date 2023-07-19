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
public @interface AutoMapper {

    /**
     * 完整的Mapper名称，指定后，将会替换掉默认生成策略
     */
    String value() default "";

    /**
     * 生成的Mapper类的后缀，前半部分固定为Entity的名字
     */
    String suffix() default "Mapper";

    /**
     * 生成Mapper所在的包名，默认与Entity包名一致
     */
    String packageName() default "";

    /**
     * 指定的Mapper的父类，通常用于自定义Mapper的场景，
     * 要求：
     * 1、值需要是类的全路径
     * 2、自定义的父类Mapper必须继承自com.baomidou.mybatisplus.core.mapper.BaseMapper
     * 3、自定义的父类Mapper必须保留泛型<T>
     */
    String baseMapperClassName() default "";
}
