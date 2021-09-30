package com.tangzc.mpe.bind;

import com.tangzc.mpe.base.event.BindEvent;
import com.tangzc.mpe.base.event.BindIPageEvent;
import com.tangzc.mpe.base.event.BindListEvent;
import org.springframework.context.event.EventListener;

/**
 * @author don
 */
public class BindEventListeners {

    @EventListener
    public <BEAN> void bindEventListener(BindEvent<BEAN> event){
        Binder.bindOn(event.getBind(), event.getBindFields());
    }

    @EventListener
    public <BEAN> void bindListEventListener(BindListEvent<BEAN> event){
        Binder.bindOn(event.getBind(), event.getBindFields());
    }

    @EventListener
    public <BEAN> void bindPageEventListener(BindIPageEvent<BEAN> event){
        Binder.bindOn(event.getBind(), event.getBindFields());
    }
}
