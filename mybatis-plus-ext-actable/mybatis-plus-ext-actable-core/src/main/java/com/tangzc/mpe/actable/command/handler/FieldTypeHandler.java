package com.tangzc.mpe.actable.command.handler;

import java.lang.reflect.Field;

public interface FieldTypeHandler {

    Class<?> getFieldType(Class<?> entityClass, Field field);
}
