package com.tangzc.mpe.base.event;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.List;

/**
 * 数据绑定操作事件
 * @author don
 */
@Getter
public class BindIPageEvent<BEAN> extends ApplicationEvent {

    private final IPage<BEAN> bind;
    private final List<SFunction<BEAN, ?>> bindFields;

    public BindIPageEvent(IPage<BEAN> bind) {
        super("");
        this.bind = bind;
        this.bindFields = Collections.emptyList();
    }

    public BindIPageEvent(IPage<BEAN> bind, List<SFunction<BEAN, ?>> bindFields) {
        super("");
        this.bind = bind;
        this.bindFields = bindFields;
    }
}
