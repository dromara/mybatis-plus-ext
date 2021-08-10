package com.tangzc.mpe.core.event;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 初始化扫描Mapper事件
 * @author don
 */
@Getter
public class InitScanMapperEvent extends ApplicationEvent {

    private final BaseMapper<?> mapper;

    public InitScanMapperEvent(BaseMapper<?> mapper) {
        super("");
        this.mapper = mapper;
    }
}
