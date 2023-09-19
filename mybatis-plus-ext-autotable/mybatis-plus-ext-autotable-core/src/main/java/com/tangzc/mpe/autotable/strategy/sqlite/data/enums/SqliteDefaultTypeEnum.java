package com.tangzc.mpe.autotable.strategy.sqlite.data.enums;

import lombok.Getter;

/**
 * @author don
 */
@Getter
public enum SqliteDefaultTypeEnum {

    INTEGER(16, null),
    REAL(10, 2),
    TEXT(null, null),
    BLOB(null, null);

    /**
     * 默认类型长度
     */
    private final Integer lengthDefault;
    /**
     * 默认小数点后长度
     */
    private final Integer decimalLengthDefault;

    SqliteDefaultTypeEnum(Integer lengthDefault, Integer decimalLengthDefault) {
        this.lengthDefault = lengthDefault;
        this.decimalLengthDefault = decimalLengthDefault;
    }

    public String typeName() {
        return name().toLowerCase();
    }
}
