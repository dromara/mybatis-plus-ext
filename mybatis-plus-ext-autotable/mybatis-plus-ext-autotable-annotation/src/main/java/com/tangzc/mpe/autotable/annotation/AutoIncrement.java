package com.tangzc.mpe.autotable.annotation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.lang.annotation.*;


/**
 * 标志该字段需要设置自增, 等同于{@link TableId#type} 设置 {@link IdType#AUTO}
 * @author don
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableId(type = IdType.AUTO)
public @interface AutoIncrement {
}
