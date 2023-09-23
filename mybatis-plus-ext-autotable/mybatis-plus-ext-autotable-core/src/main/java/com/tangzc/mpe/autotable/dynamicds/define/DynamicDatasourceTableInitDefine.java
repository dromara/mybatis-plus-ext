package com.tangzc.mpe.autotable.dynamicds.define;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.tangzc.mpe.autotable.dynamicds.impl.DynamicDatasourceHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 如果引入了baomidou的动态数据源框架，则加载该类
 * @author don
 */
@Configuration
@ConditionalOnClass(DynamicDataSourceAutoConfiguration.class)
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DynamicDatasourceTableInitDefine {

    @Bean
    public DynamicDatasourceHandler dynamicDatasourceTableInitHandler() {
        return new DynamicDatasourceHandler();
    }
}
