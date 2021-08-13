package com.tangzc.mpe.actable;

import com.tangzc.mpe.actable.manager.handler.StartUpHandlerImpl;
import com.tangzc.mpe.actable.manager.system.SysMysqlCreateTableManager;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        StartUpHandlerImpl.class,
        SysMysqlCreateTableManager.class,
        MapperScannerConfig.class,
})
public @interface EnableAutoTable {
}
