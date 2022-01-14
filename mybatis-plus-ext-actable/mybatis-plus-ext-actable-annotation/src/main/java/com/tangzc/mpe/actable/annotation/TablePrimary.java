package com.tangzc.mpe.actable.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 表字符集
 *
 * @author tangzc
 * @version 2020年11月11日 下午6:13:37
 */
//表示注解加在接口、类、枚举等
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
//VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Retention(RetentionPolicy.RUNTIME)
//将此注解包含在javadoc中
@Documented
public @interface TablePrimary {

    /**
     * 多个Entity对应一个表的情况下，需要指定哪个是主要的，true表示主要的，以此为蓝本创建表
     */
    boolean value() default false;
}
