package com.tangzc.mpe.autotable.strategy.sqlite.converter.impl;

import com.tangzc.mpe.autotable.strategy.sqlite.converter.JavaToSqliteConverter;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteTypeAndLength;
import com.tangzc.mpe.autotable.strategy.sqlite.data.enums.SqliteDefaultTypeEnum;
import com.tangzc.mpe.magic.util.EnumUtil;

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
public class DefaultJavaToSqliteConverter implements JavaToSqliteConverter {
    private static final Map<Class<?>, SqliteDefaultTypeEnum> JAVA_TO_SQLITE_TYPE_MAP = new HashMap<Class<?>, SqliteDefaultTypeEnum>() {
        {
            put(String.class, SqliteDefaultTypeEnum.TEXT);
            put(Character.class, SqliteDefaultTypeEnum.TEXT);
            put(char.class, SqliteDefaultTypeEnum.TEXT);

            put(BigInteger.class, SqliteDefaultTypeEnum.INTEGER);
            put(Long.class, SqliteDefaultTypeEnum.INTEGER);
            put(long.class, SqliteDefaultTypeEnum.INTEGER);

            put(Integer.class, SqliteDefaultTypeEnum.INTEGER);
            put(int.class, SqliteDefaultTypeEnum.INTEGER);

            put(Boolean.class, SqliteDefaultTypeEnum.INTEGER);
            put(boolean.class, SqliteDefaultTypeEnum.INTEGER);

            put(Float.class, SqliteDefaultTypeEnum.REAL);
            put(float.class, SqliteDefaultTypeEnum.REAL);
            put(Double.class, SqliteDefaultTypeEnum.REAL);
            put(double.class, SqliteDefaultTypeEnum.REAL);
            put(BigDecimal.class, SqliteDefaultTypeEnum.REAL);

            put(Date.class, SqliteDefaultTypeEnum.TEXT);
            put(java.sql.Date.class, SqliteDefaultTypeEnum.TEXT);
            put(java.sql.Timestamp.class, SqliteDefaultTypeEnum.TEXT);
            put(java.sql.Time.class, SqliteDefaultTypeEnum.TEXT);
            put(LocalDateTime.class, SqliteDefaultTypeEnum.TEXT);
            put(LocalDate.class, SqliteDefaultTypeEnum.TEXT);
            put(LocalTime.class, SqliteDefaultTypeEnum.TEXT);

            put(Short.class, SqliteDefaultTypeEnum.INTEGER);
            put(short.class, SqliteDefaultTypeEnum.INTEGER);
        }
    };

    @Override
    public SqliteTypeAndLength convert(Class<?> fieldClass) {

        if (fieldClass.isEnum()) {
            // 枚举默认设置字符串类型
            fieldClass = EnumUtil.getEnumFieldSaveDbType(fieldClass);
        }
        SqliteDefaultTypeEnum sqlType = JAVA_TO_SQLITE_TYPE_MAP.getOrDefault(fieldClass, SqliteDefaultTypeEnum.TEXT);


        if (sqlType == null) {
            throw new RuntimeException(fieldClass + "默认情况下，不支持转换到sqlite类型，如有需要请自行实现" + JavaToSqliteConverter.class.getName());
        }
        return new SqliteTypeAndLength(sqlType.getLengthDefault(), sqlType.getDecimalLengthDefault(), sqlType.typeName());
    }
}
