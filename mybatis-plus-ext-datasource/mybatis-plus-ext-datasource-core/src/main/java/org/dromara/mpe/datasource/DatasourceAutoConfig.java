package org.dromara.mpe.datasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        DataSourceManager.class,
        DataSourceInitScanEntityEventListener.class
})
public class DatasourceAutoConfig {
}
