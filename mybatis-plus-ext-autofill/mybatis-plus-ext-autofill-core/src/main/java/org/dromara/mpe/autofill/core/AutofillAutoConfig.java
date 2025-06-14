package org.dromara.mpe.autofill.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@Import({
        AutoFillMetaObjectHandler.class
})
public class AutofillAutoConfig {

}
