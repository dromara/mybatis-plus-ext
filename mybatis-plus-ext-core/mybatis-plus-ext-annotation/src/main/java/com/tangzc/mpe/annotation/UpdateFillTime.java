package com.tangzc.mpe.annotation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SQL更新的时候，自动填充当前时间
 *
 * @author don
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@TableField(fill = FieldFill.UPDATE)
@FillTime
public @interface UpdateFillTime {

    /**
     * 如果字段类型为String，需要制定字符串格式
     */
    @AliasFor(annotation = FillTime.class, attribute = "format")
    String format() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 若对象上存在值，是否覆盖
     */
    @AliasFor(annotation = FillTime.class, attribute = "override")
    boolean override() default true;
}