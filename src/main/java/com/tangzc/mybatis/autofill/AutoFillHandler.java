package com.tangzc.mybatis.autofill;

import java.lang.reflect.Field;

/**
 * @author don
 */
@FunctionalInterface
public interface AutoFillHandler<Type> {

    /**
     * 获取用户信息
     *
     * @param object 当前操作的数据对象
     * @param clazz  当前操作的数据对象的class
     * @param field  当前操作的数据对象上的字段
     * @return 用户信息
     */
    Type getVal(Object object, Class<?> clazz, Field field);
}
