package com.tangzc.mpe.magic;

import com.tangzc.mpe.magic.util.SpringContextUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@Import({
        MybatisPlusProperties.class,
        MyAnnotationHandler.class,
})
public class MagicAutoConfig {

    @Bean("springContextUtil")
    public SpringContextUtil getSpringContextUtil(ApplicationContext applicationContext) {
        return new SpringContextUtil(applicationContext);
    }
}
