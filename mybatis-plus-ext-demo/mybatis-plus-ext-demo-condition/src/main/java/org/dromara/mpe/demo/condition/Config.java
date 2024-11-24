package org.dromara.mpe.demo.condition;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.dromara.mpe.condition.DynamicConditionInterceptor;
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
