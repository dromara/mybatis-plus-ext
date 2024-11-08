package com.tangzc.mpe.demo.autofill;

import com.tangzc.mpe.annotation.handler.IAutoFillHandlerForBaseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class UserNameAutoFillHandler implements IAutoFillHandlerForBaseEntity<String> {
    public static final String userName = "唐振超";
    @Override
    public String getVal(Object object, Class clazz, Field field) {
        return userName;
    }
}
