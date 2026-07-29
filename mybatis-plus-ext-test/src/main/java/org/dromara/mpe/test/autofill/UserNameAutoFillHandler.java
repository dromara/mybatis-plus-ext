package org.dromara.mpe.test.autofill;

import org.dromara.mpe.autofill.annotation.handler.AutoFillHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class UserNameAutoFillHandler implements AutoFillHandler<String> {
    public static final String USER_NAME = "测试用户";

    @Override
    public String getVal(Object object, Class clazz, Field field) {
        return USER_NAME;
    }
}
