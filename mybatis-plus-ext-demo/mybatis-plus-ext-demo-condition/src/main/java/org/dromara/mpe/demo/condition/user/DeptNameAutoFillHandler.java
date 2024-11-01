package org.dromara.mpe.demo.condition.user;

import org.dromara.mpe.annotation.handler.AutoFillHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class DeptNameAutoFillHandler implements AutoFillHandler<String> {
    @Override
    public String getVal(Object object, Class clazz, Field field) {
        return "技术中心";
    }
}
