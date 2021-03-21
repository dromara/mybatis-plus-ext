package com.tangzc.mybatis.common;

import com.tangzc.mybatis.datasource.AnnotationScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author don
 */
@Slf4j
public class ApplicationStartListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // 初始化所有的Entity和Mapper
        EntityMapperManager.initEntityMapper();
        // 扫描所有的Entity中的DataSource注解
        AnnotationScanner.scanDataSourceAnnotation();
    }
}
