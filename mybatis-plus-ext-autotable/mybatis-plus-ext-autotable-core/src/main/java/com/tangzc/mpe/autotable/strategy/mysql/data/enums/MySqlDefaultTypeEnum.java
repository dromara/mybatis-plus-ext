package com.tangzc.mpe.autotable.strategy.mysql.data.enums;

import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlTypeConstant;
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
public enum MySqlDefaultTypeEnum {

    /**
     * 整数
     */
    INT(MysqlTypeConstant.INT, 10, null),
    TINYINT(MysqlTypeConstant.TINYINT, 3, null),
    SMALLINT(MysqlTypeConstant.SMALLINT, 5, null),
    MEDIUMINT(MysqlTypeConstant.MEDIUMINT, 7, null),
    BIGINT(MysqlTypeConstant.BIGINT, 19, null),
    /**
     * 小数
     */
    FLOAT(MysqlTypeConstant.FLOAT, 4, 2),
    DOUBLE(MysqlTypeConstant.DOUBLE, 6, 2),
    DECIMAL(MysqlTypeConstant.DECIMAL, 10, 4),
    /**
     * 字符串
     */
    CHAR(MysqlTypeConstant.CHAR, 255, null),
    VARCHAR(MysqlTypeConstant.VARCHAR, 255, null),
    TEXT(MysqlTypeConstant.TEXT, null, null),
    TINYTEXT(MysqlTypeConstant.TINYTEXT, null, null),
    MEDIUMTEXT(MysqlTypeConstant.MEDIUMTEXT, null, null),
    LONGTEXT(MysqlTypeConstant.LONGTEXT, null, null),
    /**
     * 日期
     */
    YEAR(MysqlTypeConstant.YEAR, null, null),
    TIME(MysqlTypeConstant.TIME, null, null),
    DATE(MysqlTypeConstant.DATE, null, null),
    DATETIME(MysqlTypeConstant.DATETIME, null, null),
    TIMESTAMP(MysqlTypeConstant.TIMESTAMP, null, null),
    /**
     * 二进制
     */
    BIT(MysqlTypeConstant.BIT, 1, null),
    BINARY(MysqlTypeConstant.BINARY, 1, null),
    VARBINARY(MysqlTypeConstant.VARBINARY, 1, null),
    BLOB(MysqlTypeConstant.BLOB, null, null),
    TINYBLOB(MysqlTypeConstant.TINYBLOB, null, null),
    MEDIUMBLOB(MysqlTypeConstant.MEDIUMBLOB, null, null),
    LONGBLOB(MysqlTypeConstant.LONGBLOB, null, null),
    /**
     * json
     */
    JSON(MysqlTypeConstant.JSON, null, null);

    /**
     * 默认类型长度
     */
    private final Integer lengthDefault;
    /**
     * 默认小数点后长度
     */
    private final Integer decimalLengthDefault;
    /**
     * 类型名称
     */
    private final String typeName;

    MySqlDefaultTypeEnum(String typeName, Integer lengthDefault, Integer decimalLengthDefault) {
        this.typeName = typeName;
        this.lengthDefault = lengthDefault;
        this.decimalLengthDefault = decimalLengthDefault;
    }

    public String typeName() {
        return this.typeName;
    }
}
