package com.tangzc.mybatis.autofill.annotation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@TableField(fill = FieldFill.INSERT)
public @interface DefaultValue {

    /**
     * 数据插入的时候，默认的值，如果该值为时间类型，需要设置时间格式
     * 默认值支持16种格式，如下：
     * String,
     * Integer,
     * int,
     * Long,
     * long,
     * Boolean,
     * boolean,
     * Double,
     * double,
     * Float,
     * float,
     * BigDecimal,
     * Date,
     * LocalDate,
     * LocalDateTime,
     * 枚举（仅支持枚举的名字作为默认值）
     */
    String value();

    /**
     * 如果字段类型为时间类型（Date,LocalDateTime等），需要制定字符串格式
     */
    String format() default "yyyy-MM-dd HH:mm:ss";
}
