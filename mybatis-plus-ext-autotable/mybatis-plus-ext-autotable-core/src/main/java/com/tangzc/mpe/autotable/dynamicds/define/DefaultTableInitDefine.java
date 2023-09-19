package com.tangzc.mpe.autotable.dynamicds.define;

import com.tangzc.mpe.autotable.dynamicds.IDatasourceHandler;
import com.tangzc.mpe.autotable.dynamicds.impl.DefaultDatasourceHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 如果没有{@link IDatasourceHandler}的实现类，则默认加载该类
 * @author don
 */
@Configuration
@ConditionalOnMissingBean(IDatasourceHandler.class)
public class DefaultTableInitDefine {

    @Bean
    public DefaultDatasourceHandler defaultTableInitHandler() {
        return new DefaultDatasourceHandler();
    }
}
