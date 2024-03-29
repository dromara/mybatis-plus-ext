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
 * SQL更新的时候，自动填充自定义值
 * <p>已废弃，使用{@link com.tangzc.mpe.annotation.UpdateFillData}替换
 *
 * @author don
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@TableField(fill = FieldFill.UPDATE)
@OptionUser(DefaultAuditHandler.class)
@Deprecated
public @interface UpdateOptionUser {

    /**
     * 可以自定义用户信息生成方式
     */
    @AliasFor(annotation = OptionUser.class, attribute = "value")
    Class<? extends AutoFillHandler> value();

    /**
     * 若对象上存在值，是否覆盖
     */
    @AliasFor(annotation = OptionUser.class, attribute = "override")
    boolean override() default true;
}
