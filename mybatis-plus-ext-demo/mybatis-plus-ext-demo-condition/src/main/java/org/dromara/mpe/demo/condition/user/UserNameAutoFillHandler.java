package org.dromara.mpe.demo.condition.user;

import org.dromara.mpe.annotation.handler.IAutoFillHandlerForBaseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class UserNameAutoFillHandler implements IAutoFillHandlerForBaseEntity<String> {
    @Override
    public String getVal(Object object, Class clazz, Field field) {
        return "唐振超";
    }
}
