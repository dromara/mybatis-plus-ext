package com.tangzc.mpe.bind;

import com.tangzc.mpe.core.event.BindEvent;
import com.tangzc.mpe.core.event.BindIPageEvent;
import com.tangzc.mpe.core.event.BindListEvent;
import org.springframework.context.ApplicationListener;

public class BindEventListeners {

    public static class BindEventListener<BEAN> implements ApplicationListener<BindEvent<BEAN>> {

        @Override
        public void onApplicationEvent(BindEvent<BEAN> event) {
            Binder.bindOn(event.getBind(), event.getBindFields());
        }
    }

    public static class BindListEventListener<BEAN> implements ApplicationListener<BindListEvent<BEAN>> {

        @Override
        public void onApplicationEvent(BindListEvent<BEAN> event) {
            Binder.bindOn(event.getBind(), event.getBindFields());
        }
    }

    public static class BindIPageEventListener<BEAN> implements ApplicationListener<BindIPageEvent<BEAN>> {

        @Override
        public void onApplicationEvent(BindIPageEvent<BEAN> event) {
            Binder.bindOn(event.getBind(), event.getBindFields());
        }
    }
}
