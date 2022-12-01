package com.tangzc.mpe.autotable.dynamicds.define;

import com.tangzc.mpe.autotable.dynamicds.IDynamicDatasourceHandler;
import com.tangzc.mpe.autotable.dynamicds.impl.DefaultIDynamicDatasourceHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 如果没有{@link IDynamicDatasourceHandler}的实现类，则默认加载该类
 * @author don
 */
@Configuration
@ConditionalOnMissingBean(IDynamicDatasourceHandler.class)
public class DefaultTableInitDefine {

    @Bean
    public DefaultIDynamicDatasourceHandler defaultTableInitHandler() {
        return new DefaultIDynamicDatasourceHandler();
    }
}
