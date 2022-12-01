package com.tangzc.mpe.autotable.dynamicds.define;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.tangzc.mpe.autotable.dynamicds.impl.DynamicDatasourceIDynamicDatasourceHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 如果引入了baomidou的动态数据源框架，则加载该类
 * @author don
 */
@Configuration
@ConditionalOnClass(DynamicDataSourceAutoConfiguration.class)
public class DynamicDatasourceTableInitDefine {

    @Bean
    public DynamicDatasourceIDynamicDatasourceHandler dynamicDatasourceTableInitHandler() {
        return new DynamicDatasourceIDynamicDatasourceHandler();
    }
}
