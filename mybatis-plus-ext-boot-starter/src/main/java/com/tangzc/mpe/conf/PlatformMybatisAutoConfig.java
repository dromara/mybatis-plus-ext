package com.tangzc.mpe.conf;

import com.tangzc.mpe.BaseEntityFieldTypeHandler;
import com.tangzc.mpe.base.AutoFillMetaObjectHandler;
import com.tangzc.mpe.base.MapperScanner;
import com.tangzc.mpe.base.util.SpringContextUtil;
import com.tangzc.mpe.bind.BindEventListeners;
import com.tangzc.mpe.condition.ConditionInitScanEntityEventListener;
import com.tangzc.mpe.datasource.DataSourceInitScanEntityEventListener;
import com.tangzc.mpe.datasource.DataSourceManager;
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
        DataSourceManager.class,
        DataSourceInitScanEntityEventListener.class,
        ConditionInitScanEntityEventListener.class,
        BindEventListeners.BindEventListener.class,
        BindEventListeners.BindListEventListener.class,
        BindEventListeners.BindIPageEventListener.class,
        BaseEntityFieldTypeHandler.class,
})
public class PlatformMybatisAutoConfig {

}
