package com.tangzc.mpe.demo.condition.user;

import com.tangzc.mpe.annotation.handler.AutoFillHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class DeptNameAutoFillHandler implements AutoFillHandler<String> {
    @Override
    public String getVal(Object object, Class clazz, Field field) {
        return "技术中心";
    }
}
