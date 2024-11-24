package org.dromara.mpe.demo.autofill;

import org.dromara.mpe.annotation.handler.IAutoFillHandlerForBaseEntity;
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
