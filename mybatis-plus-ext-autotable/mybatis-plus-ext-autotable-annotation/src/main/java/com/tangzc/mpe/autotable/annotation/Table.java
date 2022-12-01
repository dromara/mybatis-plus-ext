package com.tangzc.mpe.autotable.annotation;


import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;


/**
 * 创建表时的表名
 * @author don
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableName
public @interface Table {

    /**
     * 表名
     *
     * @return 表名
     */
    @AliasFor(annotation = TableName.class, attribute = "value")
    String value() default "";

    /**
     * 表注释
     */
    String comment() default "";

    /**
     * 表字符集
     */
    String charset() default "";

    /**
     * 表字符排序
     */
    String collate() default "";
    /**
     * 表引擎
     */
    String engine() default "";

    /**
     * 是否主表
     *
     * @return 表名
     */
    boolean primary() default false;

    /**
     * 数据源名称
     *
     * @return 数据源
     */
    String dsName() default "";

    /**
     * 需要排除的属性名，排除掉的属性不参与建表
     *
     * @return
     */
    String[] excludeFields() default {"serialVersionUID"};
}
