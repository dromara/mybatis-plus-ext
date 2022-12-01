package com.tangzc.mpe.autotable.strategy.mysql.handler;

import com.tangzc.mpe.autotable.strategy.mysql.data.MySqlColumnTypeEnum;

import java.lang.reflect.Field;

/**
 *
 * @author don
 */
public interface SqlTypeHandler {

    /**
     * 获取sql类型
     * @param entityClass 当前bean
     * @param field 当前字段
     * @param fieldType 当前字段类型
     * @return mysql的类型
     */
    MySqlColumnTypeEnum getSqlType(Class<?> entityClass, Field field, Class<?> fieldType);
}
