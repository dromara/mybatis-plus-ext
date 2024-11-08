package com.tangzc.mpe.demo.condition;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.tangzc.mpe.condition.DynamicConditionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new DynamicConditionInterceptor());
        return interceptor;
    }
}
