package com.tangzc.mpe.bind;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@Import({
        MyBindHandler.class
})
public class BindAutoConfig {

}
