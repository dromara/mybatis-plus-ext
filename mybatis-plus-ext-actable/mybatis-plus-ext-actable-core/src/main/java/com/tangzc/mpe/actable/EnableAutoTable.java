package com.tangzc.mpe.actable;

import com.tangzc.mpe.actable.manager.handler.StartUpHandlerImpl;
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
        StartUpHandlerImpl.class,
        SysMysqlCreateTableManager.class,
        MapperScannerConfig.class,
})
public @interface EnableAutoTable {
}
