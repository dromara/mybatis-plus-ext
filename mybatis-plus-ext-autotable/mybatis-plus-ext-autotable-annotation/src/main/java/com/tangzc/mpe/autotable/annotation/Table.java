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
     * 是否主表：在多个Bean对应同一个表的时候，指定以哪一个Bean为主，以此主Bean构建表
     *
     * @return 是否主表
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
