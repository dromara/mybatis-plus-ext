package com.tangzc.mpe.autotable.strategy.sqlite.converter.impl;

import com.tangzc.mpe.autotable.strategy.sqlite.converter.JavaToSqliteConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 如果没有{@link JavaToSqliteConverter}的实现类，则默认加载该类
 * @author don
 */
@Configuration
@ConditionalOnMissingBean(JavaToSqliteConverter.class)
public class DefaultJavaToSqliteConverterDefine {

    @Bean
    public DefaultJavaToSqliteConverter defaultJavaToSqliteConverter() {
        return new DefaultJavaToSqliteConverter();
    }
}
