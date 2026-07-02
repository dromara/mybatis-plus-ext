package org.dromara.mpe.autofill.annotation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SQL更新的时候，自动填充当前时间
 *
 * @author don
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@TableField(fill = FieldFill.UPDATE)
@FillTime
public @interface UpdateFillTime {

    /**
     * 如果字段类型为String，需要制定字符串格式
     */
    @AliasFor(annotation = FillTime.class, attribute = "format")
    String format() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 若对象上存在值，是否覆盖
     */
    @AliasFor(annotation = FillTime.class, attribute = "override")
    boolean override() default true;

    /**
     * 时区，为空时使用系统默认时区（或全局配置 mpe.default-timezone）。
     * <p>仅对 String、LocalDate、LocalDateTime 类型字段生效，
     * Date 和 Long 类型存储的是绝对时间戳，不受时区影响。</p>
     * <p>示例: "Asia/Shanghai", "UTC", "America/New_York"</p>
     */
    @AliasFor(annotation = FillTime.class, attribute = "timezone")
    String timezone() default "";
}
