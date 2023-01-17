package com.tangzc.mpe.autotable.strategy.mysql.converter.impl;

import com.tangzc.mpe.autotable.strategy.mysql.converter.JavaToMysqlConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 如果没有{@link JavaToMysqlConverter}的实现类，则默认加载该类
 * @author don
 */
@Configuration
@ConditionalOnMissingBean(JavaToMysqlConverter.class)
public class DefaultJavaToMysqlConverterDefine {

    @Bean
    public DefaultJavaToMysqlConverter defaultJavaToMysqlConverter() {
        return new DefaultJavaToMysqlConverter();
    }
}
