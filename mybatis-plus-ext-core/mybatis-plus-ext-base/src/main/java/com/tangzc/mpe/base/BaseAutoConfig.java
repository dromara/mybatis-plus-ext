package com.tangzc.mpe.base;

import com.tangzc.mpe.base.util.SpringContextUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@Import({
        SpringContextUtil.class,
        AutoFillMetaObjectHandler.class,
        MapperScanner.class,
})
public class BaseAutoConfig {

}
