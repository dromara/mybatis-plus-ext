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
}
