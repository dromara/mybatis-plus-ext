package com.tangzc.mpe.autotable.annotation;

import com.baomidou.mybatisplus.annotation.TableField;
import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 建表的必备注解
 * @author don
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableField
@ColumnType
@NotNull
@Primary
@ColumnDefault
@ColumnComment("")
public @interface Column {

    /**
     * 字段名
     *
     * @return 字段名：不填默认使用属性名作为表字段名
     */
    @AliasFor(annotation = TableField.class, attribute = "value")
    String value() default "";

    /**
     * 字段类型：不填默认使用属性的数据类型进行转换，转换失败的字段不会添加
     *
     * @return 字段类型
     */
    @AliasFor(annotation = ColumnType.class, attribute = "value")
    String type() default "";

    /**
     * 字段长度
     *
     * @return 默认字段长度，默认是255
     */
    @AliasFor(annotation = ColumnType.class, attribute = "length")
    int length() default 0;

    /**
     * 小数点长度，默认是0
     *
     * @return 小数点长度，默认是0
     */
    @AliasFor(annotation = ColumnType.class, attribute = "decimalLength")
    int decimalLength() default 0;

    /**
     * 是否为可以为null，true是可以，false是不可以，默认为true
     *
     * @return 是否为可以为null，true是不可以，false是可以，默认为false
     */
    @AliasFor(annotation = NotNull.class, attribute = "value")
    boolean notNull() default false;

    /**
     * 是否是主键，默认false
     *
     * @return 是否是主键，默认false
     */
    @AliasFor(annotation = Primary.class, attribute = "value")
    boolean isPrimary() default false;

    /**
     * 默认值，默认为null
     *
     * @return 默认值
     */
    @AliasFor(annotation = ColumnDefault.class, attribute = "type")
    DefaultValueEnum defaultValueType() default DefaultValueEnum.UNDEFINED;

    /**
     * 默认值，默认为null
     *
     * @return 默认值
     */
    @AliasFor(annotation = ColumnDefault.class, attribute = "value")
    String defaultValue() default "";

    /**
     * 数据表字段备注
     *
     * @return 默认值，默认为空
     */
    @AliasFor(annotation = ColumnComment.class, attribute = "value")
    String comment() default "";

}
