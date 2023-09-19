package com.tangzc.mpe.autotable.strategy;

import java.lang.reflect.Field;

/**
 * 对外开放的判断字段类型的接口，比如公共父类的字段类型为泛型的情况下，需要自定义获取字段类型的逻辑
 * @author don
 */
public interface FieldTypeHandler {

    /**
     * 获取字段类型
     * @param entityClass 当前bean
     * @param field 当前字段
     * @return 字段类型
     */
    Class<?> getFieldType(Class<?> entityClass, Field field);
}
