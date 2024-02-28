package com.tangzc.mpe.autotable.annotation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.autotable.annotation.ColumnComment;
import com.tangzc.autotable.annotation.ColumnType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 标志该字段为主键
 * @author don
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableId
@ColumnType("")
@ColumnComment("")
public @interface ColumnId {

    /**
     * 字段名
     *
     * @return 字段名：不填默认使用属性名作为表字段名
     */
    @AliasFor(annotation = TableId.class, attribute = "value")
    String value() default "";

    /**
     * 主键类型
     * {@link IdType}
     */
    @AliasFor(annotation = TableId.class, attribute = "type")
    IdType mode() default IdType.NONE;

    /**
     * 字段类型：不填默认使用属性的数据类型进行转换，转换失败的字段不会添加
     *
     * @return 字段类型
     */
    @AliasFor(annotation = ColumnType.class, attribute = "value")
    String type() default "";

    /**
     * 字段长度,默认是-1，小于0相当于null
     *
     * @return 默认字段长度
     */
    @AliasFor(annotation = ColumnType.class, attribute = "length")
    int length() default -1;

    /**
     * 数据表字段备注
     *
     * @return 默认值，默认为空
     */
    @AliasFor(annotation = ColumnComment.class, attribute = "value")
    String comment() default "";
}
