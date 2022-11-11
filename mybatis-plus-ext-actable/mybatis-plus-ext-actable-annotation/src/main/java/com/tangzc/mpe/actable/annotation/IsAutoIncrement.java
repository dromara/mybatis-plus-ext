package com.tangzc.mpe.actable.annotation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.lang.annotation.*;


/**
 * 标志该字段需要设置自增, @TableId(type = IdType.AUTO)的快捷方式
 *
 * @author sunchenbin
 * @version 2020年5月26日 下午6:13:15
 */
// 该注解用于方法声明
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
// VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Retention(RetentionPolicy.RUNTIME)
// 将此注解包含在javadoc中
@Documented
// 告知MybatisPlus主键自增的方式
@TableId(type = IdType.AUTO)
public @interface IsAutoIncrement {
}
