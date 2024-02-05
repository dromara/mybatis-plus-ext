package com.tangzc.mpe.magic;

import com.tangzc.mpe.magic.util.SpringContextUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@Import({
        SpringContextUtil.class,
        MybatisPlusProperties.class,
        MyAnnotationHandler.class,
})
public class MagicAutoConfig {

}
