package org.dromara.mpe.autofill.annotation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import org.dromara.mpe.autofill.annotation.handler.AutoFillHandler;
import org.dromara.mpe.autofill.annotation.handler.DefaultAuditHandler;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SQL插入的时候，自动填充自定义值
 *
 * @author don
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@TableField(fill = FieldFill.INSERT)
@FillData(DefaultAuditHandler.class)
public @interface InsertFillData {

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
