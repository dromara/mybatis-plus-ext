package com.tangzc.mpe.magic;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@Import({
        MybatisPlusProperties.class
})
public class MagicAutoConfig {

}
