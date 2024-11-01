package org.dromara.mpe.condition;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ConditionInitScanEntityEventListener.class
})
public class ConditionAutoConfig {

}
