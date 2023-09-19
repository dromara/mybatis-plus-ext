package com.tangzc.mpe.datasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        DataSourceManager.class,
        DataSourceInitScanEntityEventListener.class
})
public class DatasourceAutoConfig {
}
