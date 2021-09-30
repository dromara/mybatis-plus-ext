package com.tangzc.mpe.base.event;

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
public class BindListEvent<BEAN> extends ApplicationEvent {

    private final List<BEAN> bind;
    private final List<SFunction<BEAN, ?>> bindFields;

    public BindListEvent(List<BEAN> bind) {
        super("");
        this.bind = bind;
        this.bindFields = Collections.emptyList();
    }

    public BindListEvent(List<BEAN> bind, List<SFunction<BEAN, ?>> bindFields) {
        super("");
        this.bind = bind;
        this.bindFields = bindFields;
    }
}
