package com.tangzc.mpe.actable.manager.handler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author don
 */
@Configuration
@ConditionalOnMissingBean(TableInitHandler.class)
public class DefaultTableInitConfig {

    @Bean
    public DefaultTableInitHandler defaultTableInitHandler() {
        return new DefaultTableInitHandler();
    }
}
