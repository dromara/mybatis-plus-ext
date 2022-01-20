package com.tangzc.mpe.actable;

import com.tangzc.mpe.actable.manager.handler.DynamicDatasourceTableInitConfig;
import com.tangzc.mpe.actable.manager.handler.DefaultTableInitConfig;
import com.tangzc.mpe.actable.manager.handler.StartUpHandler;
import com.tangzc.mpe.actable.manager.system.SysMysqlCreateTableManager;
import com.tangzc.mpe.actable.utils.SpringContextUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author don
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        // 该类需要排在第一个
        SpringContextUtil.class,
        MybatisPlusProperties.class,
        MapperScannerConfig.class,
        SysMysqlCreateTableManager.class,
        StartUpHandler.class,
        DynamicDatasourceTableInitConfig.class,
        DefaultTableInitConfig.class,
})
public @interface EnableAutoTable {

    boolean dynamicDataSources() default false;
}
