package com.tangzc.mpe.actable.annotation;

import com.baomidou.mybatisplus.annotation.TableField;
import com.tangzc.mpe.actable.annotation.constants.MySqlTypeConstant;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 建表的必备注解
 *
 * @author sunchenbin, Spet
 * @version 2019/07/06
 */
// 该注解用于方法声明
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
// VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Retention(RetentionPolicy.RUNTIME)
// 将此注解包含在javadoc中
@Documented
@TableField
@ColumnType
@IsNotNull
@IsKey
@IsAutoIncrement
@ColumnDefault
@ColumnComment
public @interface Column {

    /**
     * 字段名
     * @return 字段名：不填默认使用属性名作为表字段名
     */
    @AliasFor(annotation = TableField.class, attribute = "value")
    String value() default "";

    /**
     * 字段类型：不填默认使用属性的数据类型进行转换，转换失败的字段不会添加
     * 仅支持com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant中的枚举数据类型
     * 不填默认转换类：com.tangzc.mpe.actable.command.JavaToMysqlType
     *
     * @return 字段类型
     */
    @AliasFor(annotation = ColumnType.class, attribute = "value")
    MySqlTypeConstant type() default MySqlTypeConstant.DEFAULT;

    /**
     * 字段长度，默认是255
     * 类型默认长度参考：com.tangzc.mpe.actable.constants.MySqlTypeConstant
     *
     * @return 默认字段长度，默认是255
     */
    @AliasFor(annotation = ColumnType.class, attribute = "length")
    int length() default 255;

    /**
     * 小数点长度，默认是0
     * 类型默认长度参考：com.tangzc.mpe.actable.constants.MySqlTypeConstant
     *
     * @return 小数点长度，默认是0
     */
    @AliasFor(annotation = ColumnType.class, attribute = "decimalLength")
    int decimalLength() default 0;

    /**
     * 是否为可以为null，true是可以，false是不可以，默认为true
     * 也可通过注解实现：com.tangzc.mpe.actable.annotation.IsNotNull
     *
     * @return 是否为可以为null，true是不可以，false是可以，默认为false
     */
    @AliasFor(annotation = IsNotNull.class, attribute = "value")
    boolean notNull() default false;

    /**
     * 是否是主键，默认false
     * 也可通过注解实现：com.tangzc.mpe.actable.annotation.IsKey
     *
     * @return 是否是主键，默认false
     */
    @AliasFor(annotation = IsKey.class, attribute = "value")
    boolean isKey() default false;

    /**
     * 是否自动递增，默认false
     * 也可通过注解实现：com.tangzc.mpe.actable.annotation.IsAutoIncrement
     *
     * @return 是否自动递增，默认false 只有主键才能使用
     */
    @AliasFor(annotation = IsAutoIncrement.class, attribute = "value")
    boolean isAutoIncrement() default false;

    /**
     * 默认值，默认为null
     * 也可通过注解实现：com.tangzc.mpe.actable.annotation.ColumnDefault
     *
     * @return 默认值
     */
    @AliasFor(annotation = ColumnDefault.class, attribute = "value")
    String defaultValue() default "";

    /**
     * 数据表字段备注
     * 也可通过注解实现：com.tangzc.mpe.actable.annotation.Comment
     *
     * @return 默认值，默认为空
     */
    @AliasFor(annotation = ColumnComment.class, attribute = "value")
    String comment() default "";

}
