package org.dromara.mpe.base;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import org.dromara.mpe.base.entity.BaseEntity;
import org.dromara.mpe.annotation.handler.FieldDateTypeHandler;

import java.lang.reflect.Field;

/**
 * @author don
 */
public class BaseEntityFieldTypeHandler implements FieldDateTypeHandler {

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
