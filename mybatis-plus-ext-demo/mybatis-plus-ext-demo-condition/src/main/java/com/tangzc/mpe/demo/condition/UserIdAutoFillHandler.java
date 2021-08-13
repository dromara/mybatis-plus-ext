package com.tangzc.mpe.demo.condition;

import com.tangzc.mpe.annotation.handler.AutoFillHandler;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class UserIdAutoFillHandler implements AutoFillHandler<String> {

    @Override
    public String getVal(Object object, Class<?> clazz, Field field) {

        return String.valueOf(RandomUtils.nextInt(1, 2));
    }
}
