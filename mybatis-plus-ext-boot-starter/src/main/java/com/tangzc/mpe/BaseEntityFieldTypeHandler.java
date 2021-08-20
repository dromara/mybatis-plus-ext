package com.tangzc.mpe;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.tangzc.mpe.actable.annotation.constants.MySqlTypeConstant;
import com.tangzc.mpe.actable.command.handler.FieldTypeHandler;
import com.tangzc.mpe.actable.command.handler.SqlTypeHandler;
import com.tangzc.mpe.base.base.BaseEntity;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;

public class BaseEntityFieldTypeHandler implements FieldTypeHandler, SqlTypeHandler {

    @Override
    public Class<?> getFieldType(Class<?> entityClass, Field field) {

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

    @Override
    public MySqlTypeConstant getSqlType(Class<?> entityClass, Field field, Class<?> fieldType) {
        // 这种情况下，使用了mybatis-plus的数据序列化，默认设置为字符串类型
        TableField tableField = AnnotatedElementUtils.findMergedAnnotation(field, TableField.class);
        if (tableField != null && AbstractJsonTypeHandler.class.isAssignableFrom(tableField.typeHandler())) {
            return MySqlTypeConstant.VARCHAR;
        }
        return null;
    }
}
