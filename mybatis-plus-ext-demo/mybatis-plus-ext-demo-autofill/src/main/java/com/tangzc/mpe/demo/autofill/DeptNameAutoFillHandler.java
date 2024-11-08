package com.tangzc.mpe.demo.autofill;

import com.tangzc.mpe.annotation.handler.AutoFillHandler;
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
