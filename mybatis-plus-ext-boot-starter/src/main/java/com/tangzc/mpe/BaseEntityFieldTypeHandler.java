package com.tangzc.mpe;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.tangzc.mpe.autotable.strategy.mysql.handler.FieldTypeHandler;
import com.tangzc.mpe.base.AutoFillMetaObjectHandler;
import com.tangzc.mpe.base.BaseEntity;

import java.lang.reflect.Field;

/**
 * @author don
 */
public class BaseEntityFieldTypeHandler implements FieldTypeHandler, AutoFillMetaObjectHandler.FieldDateTypeHandler {

    @Override
    public Class<?> getFieldType(Class<?> entityClass, Field field) {
        return getFiledTypeClass(entityClass, field);
    }

    @Override
    public Class<?> getDateType(Class<?> clazz, Field field) {

        // 针对BaseEntity的泛型日期类型
        return getFiledTypeClass(clazz, field);
    }

    private Class<?> getFiledTypeClass(Class<?> entityClass, Field field) {

        // 针对BaseEntity的泛型日期类型
        if (BaseEntity.class.isAssignableFrom(entityClass)) {
            String typeName = field.getGenericType().getTypeName();
            switch (typeName) {
                case "ID_TYPE":
                    return ReflectionKit.getSuperClassGenericType(entityClass, BaseEntity.class, 0);
                case "TIME_TYPE":
                    return ReflectionKit.getSuperClassGenericType(entityClass, BaseEntity.class, 1);
                default:
            }
        }
        return null;
    }
}
