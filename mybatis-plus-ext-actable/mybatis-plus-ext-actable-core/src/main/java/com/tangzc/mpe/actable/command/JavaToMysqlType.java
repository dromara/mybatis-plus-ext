package com.tangzc.mpe.actable.command;

import com.tangzc.mpe.actable.annotation.constants.MySqlTypeConstant;
import com.tangzc.mpe.actable.command.handler.FieldTypeHandler;
import com.tangzc.mpe.actable.command.handler.SqlTypeHandler;
import com.tangzc.mpe.actable.utils.SpringContextUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author don
 */
public class JavaToMysqlType {

    public static MySqlTypeConstant getSqlType(Field field, Class<?> clazz) {

        Class<?> fieldType = field.getType();

        // 自定义获取字段的类型
        List<FieldTypeHandler> fieldTypeHandlers = SpringContextUtil.getBeansOfTypeList(FieldTypeHandler.class);
        Optional<? extends Class<?>> optionalFieldType = fieldTypeHandlers.stream()
                .map(handler -> handler.getFieldType(clazz, field))
                .filter(Objects::nonNull)
                .findFirst();
        if (optionalFieldType.isPresent()) {
            fieldType = optionalFieldType.get();
        }

        // 自定义获取sql的类型
        List<SqlTypeHandler> sqlTypeHandlers = SpringContextUtil.getBeansOfTypeList(SqlTypeHandler.class);
        Class<?> finalFieldType = fieldType;
        Optional<MySqlTypeConstant> optionalSqlType = sqlTypeHandlers.stream()
                .map(handler -> handler.getSqlType(clazz, field, finalFieldType))
                .filter(Objects::nonNull)
                .findFirst();
        if (optionalSqlType.isPresent()) {
            return optionalSqlType.get();
        }

        // 枚举默认设置字符串类型
        if (fieldType.isEnum()) {
            return MySqlTypeConstant.VARCHAR;
        }

        return SqlTypeHandler.JAVA_TO_MYSQL_TYPE_MAP.get(fieldType.toString());
    }
}
