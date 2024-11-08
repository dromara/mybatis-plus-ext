package com.tangzc.mpe.annotation;

import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 排除某个字段，不被MybatisPlus管理
 * <p>区别自动建表逻辑中的com.tangzc.mpe.autotable.annotation.Ignore</p>
 * <p>@Exclude仅仅排除了数组操作，@Ignore不仅排除了数据操作，且排序数据表维护操作</p>
 *
 * @author don
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableField(exist = false)
public @interface Exclude {

}
