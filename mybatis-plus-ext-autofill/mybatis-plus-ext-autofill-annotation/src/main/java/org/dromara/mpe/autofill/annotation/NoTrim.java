package org.dromara.mpe.autofill.annotation;

import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 允许前后空白字符，不做前后去除空字符串的处理
 *
 * @author don
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableField(exist = false)
public @interface NoTrim {

}
