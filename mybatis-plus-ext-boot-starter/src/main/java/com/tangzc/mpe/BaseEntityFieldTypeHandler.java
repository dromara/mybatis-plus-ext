package com.tangzc.mpe;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.AnnotatedElementUtilsPlus;
import com.baomidou.mybatisplus.core.metadata.impl.TableFieldImpl;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.tangzc.mpe.actable.annotation.constants.MySqlTypeConstant;
import com.tangzc.mpe.actable.command.handler.FieldTypeHandler;
import com.tangzc.mpe.actable.command.handler.SqlTypeHandler;
import com.tangzc.mpe.base.AutoFillMetaObjectHandler;
import com.tangzc.mpe.base.BaseEntity;

import java.lang.reflect.Field;

public class BaseEntityFieldTypeHandler implements FieldTypeHandler, SqlTypeHandler, AutoFillMetaObjectHandler.FieldDateTypeHandler {

    @Override
    public Class<?> getFieldType(Class<?> entityClass, Field field) {
        return getFiledTypeClass(entityClass, field);

    }

    @Override
    public MySqlTypeConstant getSqlType(Class<?> entityClass, Field field, Class<?> fieldType) {
        // 这种情况下，使用了mybatis-plus的数据序列化，默认设置为字符串类型
        TableField tableField = AnnotatedElementUtilsPlus.findMergedAnnotation(field, TableField.class, TableFieldImpl.class);
        if (tableField != null && AbstractJsonTypeHandler.class.isAssignableFrom(tableField.typeHandler())) {
            return MySqlTypeConstant.VARCHAR;
        }
        return null;
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
