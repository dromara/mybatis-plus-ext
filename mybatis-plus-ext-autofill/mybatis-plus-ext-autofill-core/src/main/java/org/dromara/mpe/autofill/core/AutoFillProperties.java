package org.dromara.mpe.autofill.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("mpe")
public class AutoFillProperties {

    /**
     * 是否自动trim
     */
    private Boolean autoTrim = false;

    /**
     * 全局默认时区，注解级别未配置 timezone 时使用此值。
     * 为空时使用 JVM 系统默认时区。
     * <p>示例: "Asia/Shanghai", "UTC", "America/New_York"</p>
     */
    private String defaultTimezone = "";
}
