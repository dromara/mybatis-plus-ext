package org.dromara.mpe.demo.autofill;

import org.dromara.mpe.annotation.handler.AutoFillHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class DeptNameAutoFillHandler implements AutoFillHandler<String> {
    public static final String deptName = "技术中心";
    @Override
    public String getVal(Object object, Class clazz, Field field) {
        return deptName;
    }
}
