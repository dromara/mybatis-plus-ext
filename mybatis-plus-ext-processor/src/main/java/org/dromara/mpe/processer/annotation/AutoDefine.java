package org.dromara.mpe.processer.annotation;

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
     * 自定义完整的Define名称，指定后，将会替换掉默认生成策略
     */
    String value() default "";

    /**
     * 生成的Define类的后缀，前半部分固定为Entity的名字，默认：Define
     */
    String suffix() default "";

    /**
     * 生成Define所在的包名，默认与Entity包名一致
     */
    String packageName() default "";
}
