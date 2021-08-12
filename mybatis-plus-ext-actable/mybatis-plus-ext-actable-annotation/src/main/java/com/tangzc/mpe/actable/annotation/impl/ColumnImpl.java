package com.tangzc.mpe.actable.annotation.impl;

import com.tangzc.mpe.actable.annotation.Column;
import com.tangzc.mpe.actable.annotation.constants.MySqlTypeConstant;

import java.lang.annotation.Annotation;

public class ColumnImpl implements Column {
    /**
     * 字段名
     *
     * @return 字段名：不填默认使用属性名作为表字段名
     */
    @Override
    public String value() {
        return "";
    }

    /**
     * 字段类型：不填默认使用属性的数据类型进行转换，转换失败的字段不会添加
     * 仅支持com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant中的枚举数据类型
     * 不填默认转换类：com.tangzc.mpe.actable.command.JavaToMysqlType
     * 1.3.0版本支持，也可通过注解实现：com.tangzc.mpe.actable.annotation.ColumnType
     *
     * @return 字段类型
     */
    @Override
    public MySqlTypeConstant type() {
        return MySqlTypeConstant.DEFAULT;
    }

    /**
     * 字段长度，默认是255
     * 类型默认长度参考：com.tangzc.mpe.actable.constants.MySqlTypeConstant
     *
     * @return 默认字段长度，默认是255
     */
    @Override
    public int length() {
        return 255;
    }

    /**
     * 小数点长度，默认是0
     * 类型默认长度参考：com.tangzc.mpe.actable.constants.MySqlTypeConstant
     *
     * @return 小数点长度，默认是0
     */
    @Override
    public int decimalLength() {
        return 0;
    }

    /**
     * 是否为可以为null，true是可以，false是不可以，默认为true
     * 也可通过注解实现：com.tangzc.mpe.actable.annotation.IsNotNull
     *
     * @return 是否为可以为null，true是可以，false是不可以，默认为true
     */
    @Override
    public boolean notNull() {
        return true;
    }

    /**
     * 是否是主键，默认false
     * 也可通过注解实现：com.tangzc.mpe.actable.annotation.IsKey
     *
     * @return 是否是主键，默认false
     */
    @Override
    public boolean isKey() {
        return false;
    }

    /**
     * 是否自动递增，默认false
     * 也可通过注解实现：com.tangzc.mpe.actable.annotation.IsAutoIncrement
     *
     * @return 是否自动递增，默认false 只有主键才能使用
     */
    @Override
    public boolean isAutoIncrement() {
        return false;
    }

    /**
     * 默认值，默认为null
     * 1.3.0版本支持，也可通过注解实现：com.tangzc.mpe.actable.annotation.ColumnDefault
     *
     * @return 默认值
     */
    @Override
    public String defaultValue() {
        return "";
    }

//    /**
//     * 开启默认值原生模式
//     * 原生模式介绍：默认是false表示非原生，此时value只支持字符串形式，会将value值以字符串的形式设置到字段的默认值，例如value="aa" 即sql为 DEFAULT "aa"
//     * 如果设置isNative=true，此时如果value="current_timestamp"，即sql为 DEFAULT current_timestamp
//     *
//     * @return
//     */
//    @Override
//    public boolean isNativeDefValue() {
//        return false;
//    }

    /**
     * 数据表字段备注
     * 1.3.0版本支持，也可通过注解实现：com.tangzc.mpe.actable.annotation.Comment
     *
     * @return 默认值，默认为空
     */
    @Override
    public String comment() {
        return "";
    }

    /**
     * Returns the annotation type of this annotation.
     *
     * @return the annotation type of this annotation
     */
    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
