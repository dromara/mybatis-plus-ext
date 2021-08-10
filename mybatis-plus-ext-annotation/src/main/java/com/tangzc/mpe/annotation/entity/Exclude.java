package com.tangzc.mpe.annotation.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableField(exist=false)
public @interface Exclude {

}
