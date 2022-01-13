package com.tangzc.mpe.actable.manager.handler;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author don
 */
@Configuration
@ConditionalOnClass(DynamicDataSourceAutoConfiguration.class)
public class DynamicDatasourceTableInitConfig {

    @Bean
    public DynamicDatasourceTableInitHandler dynamicDatasourceTableInitHandler() {
        return new DynamicDatasourceTableInitHandler();
    }
}
