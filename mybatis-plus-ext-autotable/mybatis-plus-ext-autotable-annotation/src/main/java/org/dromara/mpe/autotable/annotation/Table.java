package org.dromara.mpe.autotable.annotation;


import com.baomidou.mybatisplus.annotation.TableName;
import com.tangzc.autotable.annotation.AutoTable;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 创建表时的表名
 *
 * @author don
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableName
@AutoTable
public @interface Table {

    /**
     * 表名
     *
     * @return 表名
     */
    @AliasFor(annotation = TableName.class, attribute = "value")
    String value() default "";

    /**
     * 表schema
     */
    @AliasFor(annotation = TableName.class, attribute = "schema")
    String schema() default "";

    /**
     * 表注释
     */
    @AliasFor(annotation = AutoTable.class, attribute = "comment")
    String comment() default "";

    /**
     * 数据源名称
     *
     * @return 数据源
     */
    String dsName() default "";

    /**
     * 需要排除的属性名，字段不作为数据库维护的列，同时数据操作的时候也会忽略
     * 具备{@link TableName#excludeProperty()}的作用
     *
     * @return
     */
    @AliasFor(annotation = TableName.class, attribute = "excludeProperty")
    String[] excludeProperty() default {"serialVersionUID"};
}
