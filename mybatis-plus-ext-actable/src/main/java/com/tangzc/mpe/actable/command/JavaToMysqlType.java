package com.tangzc.mpe.actable.command;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.tangzc.mpe.annotation.constants.MySqlTypeConstant;
import com.tangzc.mpe.core.base.BaseEntity;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author don
 */
public class JavaToMysqlType {

    private static final Map<String, MySqlTypeConstant> JAVA_TO_MYSQL_TYPE_MAP = new HashMap<>();

    static {
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.lang.String", MySqlTypeConstant.VARCHAR);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.lang.Long", MySqlTypeConstant.BIGINT);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.lang.Integer", MySqlTypeConstant.INT);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.lang.Boolean", MySqlTypeConstant.BIT);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.math.BigInteger", MySqlTypeConstant.BIGINT);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.lang.Float", MySqlTypeConstant.FLOAT);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.lang.Double", MySqlTypeConstant.DOUBLE);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.lang.Short", MySqlTypeConstant.SMALLINT);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.math.BigDecimal", MySqlTypeConstant.DECIMAL);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.sql.Date", MySqlTypeConstant.DATE);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.util.Date", MySqlTypeConstant.DATE);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.sql.Timestamp", MySqlTypeConstant.DATETIME);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.sql.Time", MySqlTypeConstant.TIME);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.time.LocalDateTime", MySqlTypeConstant.DATETIME);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.time.LocalDate", MySqlTypeConstant.DATE);
        JAVA_TO_MYSQL_TYPE_MAP.put("class java.time.LocalTime", MySqlTypeConstant.TIME);
        JAVA_TO_MYSQL_TYPE_MAP.put("long", MySqlTypeConstant.BIGINT);
        JAVA_TO_MYSQL_TYPE_MAP.put("int", MySqlTypeConstant.INT);
        JAVA_TO_MYSQL_TYPE_MAP.put("boolean", MySqlTypeConstant.BIT);
        JAVA_TO_MYSQL_TYPE_MAP.put("float", MySqlTypeConstant.FLOAT);
        JAVA_TO_MYSQL_TYPE_MAP.put("double", MySqlTypeConstant.DOUBLE);
        JAVA_TO_MYSQL_TYPE_MAP.put("short", MySqlTypeConstant.SMALLINT);
        JAVA_TO_MYSQL_TYPE_MAP.put("char", MySqlTypeConstant.VARCHAR);
    }

    public static MySqlTypeConstant getSqlType(Field field, Class<?> clazz) {

        Class<?> fieldType = field.getType();

        // 针对BaseEntity的泛型日期类型
        if (BaseEntity.class.isAssignableFrom(clazz)) {
            String typeName = field.getGenericType().getTypeName();
            switch (typeName) {
                case "ID_TYPE":
                    fieldType = ReflectionKit.getSuperClassGenericType(clazz, 0);
                    break;
                case "TIME_TYPE":
                    fieldType = ReflectionKit.getSuperClassGenericType(clazz, 1);
                    break;
                default:
            }
        }

        // 枚举默认设置字符串类型
        if (fieldType.isEnum()) {
            return MySqlTypeConstant.VARCHAR;
        }
        // 这种情况下，使用了mybatis-plus的数据序列化，默认设置为字符串类型
        TableField tableField = AnnotationUtils.findAnnotation(field, TableField.class);
        if (tableField != null && AbstractJsonTypeHandler.class.isAssignableFrom(tableField.typeHandler())) {
            return MySqlTypeConstant.VARCHAR;
        }

        return JAVA_TO_MYSQL_TYPE_MAP.get(fieldType.toString());
    }
}
