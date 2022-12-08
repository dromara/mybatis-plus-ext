package com.tangzc.mpe.autotable.strategy.sqlite.data.dbdata;

import com.tangzc.mpe.autotable.strategy.sqlite.data.enums.SqliteTypeEnum;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JavaToSqliteType {

    private static final Map<Class<?>, SqliteTypeEnum> TYPE_MAP = new HashMap<Class<?>, SqliteTypeEnum>() {
        {
            put(String.class, SqliteTypeEnum.TEXT);
            put(Character.class, SqliteTypeEnum.TEXT);
            put(char.class, SqliteTypeEnum.TEXT);

            put(BigInteger.class, SqliteTypeEnum.INTEGER);
            put(Long.class, SqliteTypeEnum.INTEGER);
            put(long.class, SqliteTypeEnum.INTEGER);

            put(Integer.class, SqliteTypeEnum.INTEGER);
            put(int.class, SqliteTypeEnum.INTEGER);

            put(Boolean.class, SqliteTypeEnum.INTEGER);
            put(boolean.class, SqliteTypeEnum.INTEGER);

            put(Float.class, SqliteTypeEnum.REAL);
            put(float.class, SqliteTypeEnum.REAL);
            put(Double.class, SqliteTypeEnum.REAL);
            put(double.class, SqliteTypeEnum.REAL);
            put(BigDecimal.class, SqliteTypeEnum.REAL);

            put(Date.class, SqliteTypeEnum.TEXT);
            put(java.sql.Date.class, SqliteTypeEnum.TEXT);
            put(java.sql.Timestamp.class, SqliteTypeEnum.TEXT);
            put(java.sql.Time.class, SqliteTypeEnum.TEXT);
            put(LocalDateTime.class, SqliteTypeEnum.TEXT);
            put(LocalDate.class, SqliteTypeEnum.TEXT);
            put(LocalTime.class, SqliteTypeEnum.TEXT);

            put(Short.class, SqliteTypeEnum.INTEGER);
            put(short.class, SqliteTypeEnum.INTEGER);
        }
    };

    public static SqliteTypeEnum getSqlType(Class<?> fieldType) {

        // 枚举默认设置字符串类型
        if (fieldType.isEnum()) {
            return SqliteTypeEnum.TEXT;
        }

        return TYPE_MAP.getOrDefault(fieldType, SqliteTypeEnum.TEXT);
    }
}
