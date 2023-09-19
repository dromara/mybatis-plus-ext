package com.tangzc.mpe.autotable.strategy.pgsql.converter.impl;

import com.tangzc.mpe.autotable.strategy.pgsql.converter.JavaToPgsqlConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 如果没有{@link JavaToPgsqlConverter}的实现类，则默认加载该类
 * @author don
 */
@Configuration
@ConditionalOnMissingBean(JavaToPgsqlConverter.class)
public class DefaultJavaToPgsqlConverterDefine {

    @Bean
    public DefaultJavaToPgsqlConverter defaultJavaToPgsqlConverter() {
        return new DefaultJavaToPgsqlConverter();
    }
}
