package org.dromara.mpe.base;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@Import({
        EntityScanner.class
})
public class BaseAutoConfig {

}
