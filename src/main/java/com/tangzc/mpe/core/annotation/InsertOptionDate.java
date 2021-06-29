package com.tangzc.mpe.core.annotation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import org.springframework.core.annotation.AliasFor;

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
@OptionDate
public @interface InsertOptionDate {

    /**
     * 如果字段类型为String，需要制定字符串格式
     */
    @AliasFor(annotation = OptionDate.class, attribute = "format")
    String format() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 若对象上存在值，是否覆盖
     */
    @AliasFor(annotation = OptionDate.class, attribute = "override")
    boolean override() default true;
}
