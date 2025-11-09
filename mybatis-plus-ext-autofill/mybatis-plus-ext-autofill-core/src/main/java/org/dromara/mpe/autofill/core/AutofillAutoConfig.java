package org.dromara.mpe.autofill.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@Import({
        AutoFillMetaObjectHandler.class
})
@EnableConfigurationProperties(AutoFillProperties.class)
public class AutofillAutoConfig {

}
