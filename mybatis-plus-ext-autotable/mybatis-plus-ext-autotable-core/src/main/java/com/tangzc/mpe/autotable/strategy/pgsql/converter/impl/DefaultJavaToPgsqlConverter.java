package com.tangzc.mpe.autotable.strategy.pgsql.converter.impl;

import com.tangzc.mpe.autotable.strategy.pgsql.converter.JavaToPgsqlConverter;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlTypeAndLength;
import com.tangzc.mpe.autotable.strategy.pgsql.data.enums.PgsqlDefaultTypeEnum;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author don
 */
public class DefaultJavaToPgsqlConverter implements JavaToPgsqlConverter {
    public static final Map<Class<?>, PgsqlDefaultTypeEnum> JAVA_TO_MYSQL_TYPE_MAP = new HashMap<Class<?>, PgsqlDefaultTypeEnum>() {{
        put(String.class, PgsqlDefaultTypeEnum.VARCHAR);
        put(Character.class, PgsqlDefaultTypeEnum.CHAR);
        put(char.class, PgsqlDefaultTypeEnum.CHAR);

        put(BigInteger.class, PgsqlDefaultTypeEnum.INT8);
        put(Long.class, PgsqlDefaultTypeEnum.INT8);
        put(long.class, PgsqlDefaultTypeEnum.INT8);

        put(Integer.class, PgsqlDefaultTypeEnum.INT4);
        put(int.class, PgsqlDefaultTypeEnum.INT4);

        put(Boolean.class, PgsqlDefaultTypeEnum.BOOL);
        put(boolean.class, PgsqlDefaultTypeEnum.BOOL);

        put(Float.class, PgsqlDefaultTypeEnum.FLOAT4);
        put(float.class, PgsqlDefaultTypeEnum.FLOAT4);
        put(Double.class, PgsqlDefaultTypeEnum.FLOAT8);
        put(double.class, PgsqlDefaultTypeEnum.FLOAT8);
        put(BigDecimal.class, PgsqlDefaultTypeEnum.NUMERIC);

        put(Date.class, PgsqlDefaultTypeEnum.TIMESTAMP);
        put(java.sql.Date.class, PgsqlDefaultTypeEnum.TIMESTAMP);
        put(java.sql.Timestamp.class, PgsqlDefaultTypeEnum.TIMESTAMP);
        put(java.sql.Time.class, PgsqlDefaultTypeEnum.TIME);
        put(LocalDateTime.class, PgsqlDefaultTypeEnum.TIMESTAMP);
        put(LocalDate.class, PgsqlDefaultTypeEnum.DATE);
        put(LocalTime.class, PgsqlDefaultTypeEnum.TIME);

        put(Short.class, PgsqlDefaultTypeEnum.INT2);
        put(short.class, PgsqlDefaultTypeEnum.INT2);
    }};

    @Override
    public PgsqlTypeAndLength convert(Class<?> fieldClass) {
        PgsqlDefaultTypeEnum sqlType;
        // 枚举默认设置字符串类型
        if (fieldClass.isEnum()) {
            sqlType = PgsqlDefaultTypeEnum.VARCHAR;
        } else {
            sqlType = JAVA_TO_MYSQL_TYPE_MAP.getOrDefault(fieldClass, PgsqlDefaultTypeEnum.VARCHAR);
        }

        if (sqlType == null) {
            throw new RuntimeException(fieldClass + "默认情况下，不支持转换到pgsql类型，如有需要请自行实现" + JavaToPgsqlConverter.class.getName());
        }
        return new PgsqlTypeAndLength(sqlType.getLengthDefault(), sqlType.getDecimalLengthDefault(), sqlType.name().toLowerCase());
    }
}
