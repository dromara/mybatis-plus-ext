package com.tangzc.mpe.actable.annotation;


import java.lang.annotation.*;


/**
 * 数据源名称
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
public @interface DsName {

    /**
     * 数据源名称
     */
    String value();
}
