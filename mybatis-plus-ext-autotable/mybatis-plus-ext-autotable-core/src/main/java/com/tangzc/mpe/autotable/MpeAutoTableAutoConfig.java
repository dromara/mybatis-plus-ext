package com.tangzc.mpe.autotable;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.tangzc.autotable.core.AutoTableOrmFrameAdapter;
import com.tangzc.mpe.annotation.handler.FieldDateTypeHandler;
import com.tangzc.mpe.magic.util.SpringContextUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.stream.Collectors;

/**
 * @author don
 */
@Configuration
@AutoConfigureAfter(MybatisPlusAutoConfiguration.class)
@Import({
        SpringContextUtil.class,
        DynamicDatasourceHandler.class,
        DefaultDatasourceHandler.class,
})
public class MpeAutoTableAutoConfig {

    @Bean
    public AutoTableOrmFrameAdapter mybatisPlusAdapter(ObjectProvider<IgnoreExt> ignoreExts, ObjectProvider<FieldDateTypeHandler> fieldDateTypeHandlers) {
        return new MybatisPlusAdapter(ignoreExts.stream().collect(Collectors.toList()), fieldDateTypeHandlers.stream().collect(Collectors.toList()));
    }
}
