package org.dromara.mpe.base;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@Import({
        AutoFillMetaObjectHandler.class,
        AutoFillMetaObjectHandler.AutoFillMetaObjectHandlerChecker.class,
        EntityScanner.class
})
public class BaseAutoConfig {

}
