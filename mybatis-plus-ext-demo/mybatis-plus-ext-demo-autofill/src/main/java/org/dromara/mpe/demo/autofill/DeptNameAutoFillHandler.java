package org.dromara.mpe.demo.autofill;

import org.dromara.mpe.autofill.annotation.handler.AutoFillHandler;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Primary
@Component
public class DeptNameAutoFillHandler implements AutoFillHandler<String> {
    public static final String deptName = "技术中心";
    @Override
    public String getVal(Object object, Class clazz, Field field) {
        return deptName;
    }
}
