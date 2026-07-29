package org.dromara.mpe.test.autofill;

import org.dromara.mpe.autofill.annotation.handler.AutoFillHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class DeptNameAutoFillHandler implements AutoFillHandler<String> {
    public static final String DEPT_NAME = "技术中心";

    @Override
    public String getVal(Object object, Class clazz, Field field) {
        return DEPT_NAME;
    }
}
