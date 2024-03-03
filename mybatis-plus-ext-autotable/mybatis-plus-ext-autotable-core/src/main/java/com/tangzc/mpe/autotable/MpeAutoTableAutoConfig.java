package com.tangzc.mpe.autotable;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.tangzc.autotable.core.AutoTableOrmFrameAdapter;
import com.tangzc.mpe.magic.util.SpringContextUtil;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@AutoConfigureAfter(MybatisPlusAutoConfiguration.class)
@Import({
        DynamicDatasourceHandler.class,
        DefaultDatasourceHandler.class,
})
public class MpeAutoTableAutoConfig {

    @Bean
    public AutoTableOrmFrameAdapter mybatisPlusAdapter() {
        return new MybatisPlusAdapter();
    }

    // @Bean
    // public DynamicDatasourceHandler dynamicDatasourceHandler() {
    //     return new DynamicDatasourceHandler();
    // }
    //
    // @Bean
    // public DefaultDatasourceHandler defaultDatasourceHandler() {
    //     return new DefaultDatasourceHandler();
    // }

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }
}
