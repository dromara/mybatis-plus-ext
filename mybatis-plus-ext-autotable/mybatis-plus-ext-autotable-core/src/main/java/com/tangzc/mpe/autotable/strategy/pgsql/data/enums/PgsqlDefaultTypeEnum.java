package com.tangzc.mpe.autotable.strategy.pgsql.data.enums;

import lombok.Getter;

/**
 * @author don
 */
@Getter
public enum PgsqlDefaultTypeEnum {
    /**
     * 整数
     */
    INT2(11, null), //
    INT4(11, null), // int
    INT8(32, null), // long
    /**
     * 布尔
     */
    BOOL(null, null), // Boolean
    /**
     * 小数
     */
    FLOAT4(10, 2), // float
    FLOAT8(10, 2), // double
    MONEY(null, null), //
    DECIMAL(10, 6), //
    NUMERIC(10, 6), // BigDecimal
    /**
     * 字节
     */
    BYTEA(null, null), // byte
    /**
     * 字符串
     */
    CHAR(255, null), //
    VARCHAR(255, null), // String
    TEXT(null, null), //
    /**
     * 日期
     */
    TIME(null, null), // java.sql.Time、LocalTime
    DATE(null, null), // LocalDate
    TIMESTAMP(null, null), // java.sql.Timestamp、Date、LocalDateTime
    /**
     * 二进制
     */
    BIT(1, null), //
    BLOB(null, null), // Blob
    CLOB(null, null), // Clob

    ;

    /**
     * 默认类型长度
     */
    private final Integer lengthDefault;
    /**
     * 默认小数点后长度
     */
    private final Integer decimalLengthDefault;

    PgsqlDefaultTypeEnum(Integer lengthDefault, Integer decimalLengthDefault) {
        this.lengthDefault = lengthDefault;
        this.decimalLengthDefault = decimalLengthDefault;
    }

    public String typeName() {
        return this.name().toLowerCase();
    }
}
