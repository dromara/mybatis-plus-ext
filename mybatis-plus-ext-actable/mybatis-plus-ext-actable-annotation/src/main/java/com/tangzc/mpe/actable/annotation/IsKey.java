package com.tangzc.mpe.actable.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 标志该字段为主键
 * 也可通过注解：com.tangzc.mpe.actable.annotation.Column的isKey属性实现
 *
 * @author sunchenbin
 * @version 2020年5月26日 下午6:13:15
 */
// 该注解用于方法声明
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
// VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Retention(RetentionPolicy.RUNTIME)
// 将此注解包含在javadoc中
@Documented
public @interface IsKey {

    boolean value() default false;
}
