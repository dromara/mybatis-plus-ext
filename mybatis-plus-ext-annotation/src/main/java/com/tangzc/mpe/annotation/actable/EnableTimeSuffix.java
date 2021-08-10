package com.tangzc.mpe.annotation.actable;


import com.tangzc.mpe.annotation.constants.DateTimeFormatConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表名时间后缀
 *
 * @author Elphen
 * @version 2021年03月11日 上午11:17:37
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableTimeSuffix {
    /**
     * 开启时间后缀
     *
     * @return
     */
    boolean value() default true;

    /**
     * 时间后缀格式
     * <br> 使用常量类 {@link DateTimeFormatConstant}
     *
     * @return
     */
    String pattern() default DateTimeFormatConstant.DATE_MONTH;
}
