package com.tangzc.mpe.autotable.strategy.mysql.data.enums;

import lombok.Getter;

/**
 * 用于配置Mysql数据库中类型，并且该类型需要设置几个长度
 * 这里配置多少个类型决定了，创建表能使用多少类型
 * 例如：varchar(1)
 * decimal(5,2)
 * datetime
 *
 * @author don
 */
@Getter
public enum MySqlColumnTypeEnum {

    /**
     * 整数
     */
    INT(10, null),
    TINYINT(3, null),
    SMALLINT(5, null),
    MEDIUMINT(7, null),
    BIGINT(19, null),
    /**
     * 小数
     */
    FLOAT(4, 2),
    DOUBLE(6, 2),
    DECIMAL(10, 4),
    /**
     * 字符串
     */
    CHAR(255, null),
    VARCHAR(255, null),
    TEXT(null, null),
    TINYTEXT(null, null),
    MEDIUMTEXT(null, null),
    LONGTEXT(null, null),
    /**
     * 日期
     */
    YEAR(null, null),
    TIME(null, null),
    DATE(null, null),
    DATETIME(null, null),
    TIMESTAMP(null, null),
    /**
     * 二进制
     */
    BIT(1, null),
    BINARY(1, null),
    VARBINARY(1, null),
    BLOB(null, null),
    TINYBLOB(null, null),
    MEDIUMBLOB(null, null),
    LONGBLOB(null, null),
    /**
     * json
     */
    JSON(null, null);

    /**
     * 默认类型长度
     */
    private final Integer lengthDefault;
    /**
     * 默认小数点后长度
     */
    private final Integer decimalLengthDefault;

    MySqlColumnTypeEnum(Integer lengthDefault, Integer decimalLengthDefault) {
        this.lengthDefault = lengthDefault;
        this.decimalLengthDefault = decimalLengthDefault;
    }

    public static MySqlColumnTypeEnum parseByLowerCaseName(String name) {
        for (MySqlColumnTypeEnum type : MySqlColumnTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new RuntimeException(name + "是MySql不支持的字段数据类型");
    }
}
