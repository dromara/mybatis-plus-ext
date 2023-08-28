package com.tangzc.mpe.autotable.strategy.pgsql.data.dbdata;

import lombok.Data;

/**
 * pgsql数据库，字段信息
 */
@Data
public class PgsqlDbColumn {

    /**
     * 是否是主键
     */
    private boolean primary;
    /**
     * 列注释
     */
    private String description;
    /**
     * 列所属的数据库名称。
     */
    private String tableCatalog;
    /**
     * 列所属的模式（命名空间）名称。
     */
    private String tableSchema;
    /**
     * 列所属的表名称。
     */
    private String tableName;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列顺序
     */
    private String ordinalPosition;
    /**
     * 列默认值
     */
    private String columnDefault;
    /**
     * 是否允许为null
     */
    private String isNullable;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 如果数据类型是字符类型，表示字符的最大长度
     */
    private String characterMaximumLength;
    /**
     * 如果数据类型是字符类型，表示以字节为单位的最大长度。
     */
    private String characterOctetLength;
    /**
     * 如果数据类型是数值类型，表示数值的总位数。
     */
    private String numericPrecision;
    /**
     * 如果数据类型是数值类型，表示数值的基数（通常为 2 或 10）
     */
    private String numericPrecisionRadix;
    /**
     * 如果数据类型是数值类型，表示小数部分的位数
     */
    private String numericScale;
    /**
     * 如果数据类型是日期/时间类型，表示日期或时间的小数部分的位数
     */
    private String datetimePrecision;
    private String intervalType;
    private String intervalPrecision;
    private String characterSetCatalog;
    private String characterSetSchema;
    private String characterSetName;
    private String collationCatalog;
    private String collationSchema;
    private String collationName;
    private String domainCatalog;
    private String domainSchema;
    private String domainName;
    private String udtCatalog;
    private String udtSchema;
    private String udtName;
    private String scopeCatalog;
    private String scopeSchema;
    private String scopeName;
    private String maximumCardinality;
    private String dtdIdentifier;
    private String isSelfReferencing;
    private String isIdentity;
    private String identityGeneration;
    private String identityStart;
    private String identityIncrement;
    private String identityMaximum;
    private String identityMinimum;
    private String identityCycle;
    private String isGenerated;
    private String generationExpression;
    private String isUpdatable;

    public String getDataTypeFormat() {
        switch (this.udtName) {
            // 数字
            case "int2":
            case "int4":
            case "int8":
                return this.udtName + "(" + this.numericPrecision + ")";
            case "numeric":
                return this.udtName + "(" + this.numericPrecision + "," + this.numericScale + ")";
            // 字符串
            case "varchar":
                return this.udtName + "(" + this.characterMaximumLength + ")";
            case "bpchar":
                return "char(" + this.characterMaximumLength + ")";
            // 其他的没有长度
            default:
                return this.udtName;
        }
    }
}
