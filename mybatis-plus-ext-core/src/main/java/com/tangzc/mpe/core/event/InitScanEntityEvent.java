package com.tangzc.mpe.core.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 初始化扫描实体事件
 * @author don
 */
@Getter
public class InitScanEntityEvent extends ApplicationEvent {

    private final Class<?> entityClass;
    
    public InitScanEntityEvent(Class<?> entityClass) {
        super("");
        this.entityClass = entityClass;
    }
}
