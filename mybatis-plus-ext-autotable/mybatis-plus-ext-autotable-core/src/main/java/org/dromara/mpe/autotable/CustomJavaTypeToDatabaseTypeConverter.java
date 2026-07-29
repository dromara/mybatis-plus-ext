package org.dromara.mpe.autotable;

import com.baomidou.mybatisplus.annotation.TableField;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.dromara.autotable.core.converter.JavaTypeToDatabaseTypeConverter;
import org.dromara.mpe.magic.util.AnnotatedElementUtilsPlus;
import org.dromara.mpe.magic.util.EnumUtil;

import java.lang.reflect.Field;

public class CustomJavaTypeToDatabaseTypeConverter implements JavaTypeToDatabaseTypeConverter {

    @Override
    public Class<?> getFieldType(Class<?> clazz, Field field) {
        // 枚举，按照字符串处理
        if (field.getType().isEnum()) {
            return EnumUtil.getEnumFieldSaveDbType(field.getType());
        }
        TableField column = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableField.class);
        // json数据，按照字符串处理
        if (column != null && column.typeHandler() != UnknownTypeHandler.class) {
            return String.class;
        }

        return field.getType();
    }
}
