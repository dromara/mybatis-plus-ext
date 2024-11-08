package com.tangzc.mpe.annotation.handler;

import java.lang.reflect.Field;

@FunctionalInterface
public interface FieldDateTypeHandler {
    Class<?> getDateType(Class<?> clazz, Field field);
}
