package com.tangzc.mpe.demo.condition.user;

import com.tangzc.mpe.annotation.handler.IAutoFillHandlerForBaseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class UserNameAutoFillHandler implements IAutoFillHandlerForBaseEntity<String> {
    @Override
    public String getVal(Object object, Class clazz, Field field) {
        return "唐振超";
    }
}
