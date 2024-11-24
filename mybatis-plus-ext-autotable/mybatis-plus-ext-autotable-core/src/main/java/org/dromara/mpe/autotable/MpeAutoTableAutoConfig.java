package org.dromara.mpe.autotable;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.dromara.autotable.core.AutoTableOrmFrameAdapter;
import org.dromara.mpe.annotation.handler.FieldDateTypeHandler;
import org.dromara.mpe.magic.util.SpringContextUtil;
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
        CustomRunStateCallback.class,
})
public class MpeAutoTableAutoConfig {

    @Bean
    public AutoTableOrmFrameAdapter mybatisPlusAdapter(ObjectProvider<IgnoreExt> ignoreExts, ObjectProvider<FieldDateTypeHandler> fieldDateTypeHandlers) {
        return new MybatisPlusAdapter(ignoreExts.stream().collect(Collectors.toList()), fieldDateTypeHandlers.stream().collect(Collectors.toList()));
    }
}
