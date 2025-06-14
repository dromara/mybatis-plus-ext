package org.dromara.mpe.autotable;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.dromara.autotable.core.AutoTableMetadataAdapter;
import org.dromara.autotable.core.converter.JavaTypeToDatabaseTypeConverter;
import org.dromara.mpe.autofill.annotation.handler.FieldDateTypeHandler;
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
        CustomInitializeBeans.class,
        DynamicDatasourceHandler.class,
        CustomAutoTableClassScanner.class,
        CustomDataSourceInfoExtractor.class,
})
public class MpeAutoTableAutoConfig {

    @Bean
    public AutoTableMetadataAdapter customAutoTableMetadataAdapter(ObjectProvider<IgnoreExt> ignoreExts) {
        return new CustomAutoTableMetadataAdapter(ignoreExts.stream().collect(Collectors.toList()));
    }

    @Bean
    public JavaTypeToDatabaseTypeConverter customJavaTypeToDatabaseTypeConverter(ObjectProvider<FieldDateTypeHandler> fieldDateTypeHandlers) {
        return new CustomJavaTypeToDatabaseTypeConverter(fieldDateTypeHandlers.stream().collect(Collectors.toList()));
    }
}
