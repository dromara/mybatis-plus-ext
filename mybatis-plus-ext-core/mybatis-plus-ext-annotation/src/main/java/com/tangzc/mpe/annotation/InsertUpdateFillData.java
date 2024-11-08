package com.tangzc.mpe.annotation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.tangzc.mpe.annotation.handler.AutoFillHandler;
import com.tangzc.mpe.annotation.handler.DefaultAuditHandler;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SQL插入、更新的时候，自动填充自定义值
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@TableField(fill = FieldFill.INSERT_UPDATE)
@FillData(DefaultAuditHandler.class)
public @interface InsertUpdateFillData {

    /**
     * 可以自定义信息生成方式
     */
    @AliasFor(annotation = FillData.class, attribute = "value")
    Class<? extends AutoFillHandler> value();

    /**
     * 若对象上存在值，是否覆盖
     */
    @AliasFor(annotation = FillData.class, attribute = "override")
    boolean override() default true;
}
