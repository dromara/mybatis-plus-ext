package com.tangzc.mpe.annotation;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示字段可被序列化
 *
 * @author don
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@TableField
public @interface Serializable {

    /**
     * 注意：需要配合 @TableName(autoResultMap = true)
     * 如果序列化的为【对象(非基本类型)集合】，请手动声明一个typeHandler，继承JacksonTypeHandler即可，
     * 然后在类头上标注@MappedTypes({对象1.class,对象2.class})
     */
    @AliasFor(annotation = TableField.class, attribute = "typeHandler")
    Class<? extends TypeHandler> typeHandler() default JacksonTypeHandler.class;
}
