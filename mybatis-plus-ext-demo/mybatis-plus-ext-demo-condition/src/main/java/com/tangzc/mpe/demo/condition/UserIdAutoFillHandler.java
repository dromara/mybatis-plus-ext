package com.tangzc.mpe.demo.condition;

import com.tangzc.mpe.annotation.handler.AutoFillHandler;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class UserIdAutoFillHandler implements AutoFillHandler<Integer> {

    @Override
    public Integer getVal(Object object, Class<?> clazz, Field field) {

        return RandomUtils.nextInt(1, 2);
    }
}
