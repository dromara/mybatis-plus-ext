package com.tangzc.mpe.autotable.strategy.mysql.data.metadata;

import lombok.Data;

/**
 * 用于查询表中字段结构详细信息
 * 该对象，主要被用于判断列自身信息的变化，不包含主键和索引的判断
 * @author don
 */
@Data
public class InformationSchemaColumn {
    /**
     * 以字符为单位的最大长度，适于二进制数据、字符数据，或者文本和图像数据。否则，返回 null。有关更多信息，请参见数据类型
     */
    private Long characterMaximumLength;
    /**
     * 以字节为单位的最大长度，适于二进制数据、字符数据，或者文本和图像数据。否则，返回 nu
     */
    private Long characterOctetLength;
    /**
     * 如果该列是字符数据或 text 数据类型，那么为字符集返回唯一的名称。否则，返回 null
     */
    private String characterSetName;
    /**
     * 如果列是字符数据或 text 数据类型，那么为排序次序返回唯一的名称。否则，返回 null。
     */
    private String collationName;
    /**
     * 列的注释
     */
    private String columnComment;
    /**
     * 列的默认值
     */
    private String columnDefault;
    /**
     * <p>该列显示列是否被索引，其有如下可能值
     * <p>空: 代表没有被索引，或者是一个多列的非唯一的索引的次要列
     * <p>PRI: 代表是主键，或者是一个多列主键的其中一个栏位
     * <p>UNI: 代表是一个唯一索引的第一个列，一个唯一索引是可以有多个空值的
     * <p>MUL: 代表该列是一个非唯一索引的第一个列
     * <p>如果一个栏位在多个索引中，COLUMN_KEY只会显示其中优先级最高的一个，顺序为PRI, UNI, MUL
     * <p>如果表中无主键，如果一个唯一索引不可以包含空值(定义非空)，该列其可能会被显示为PRI
     * <p>一个复合索引如果是唯一的，该列也有可能会被显示为MUL
     */
    private String columnKey;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列的数据类型，除了类型外可能包含其他信息，例如精度等
     */
    private String columnType;
    /**
     * 系统提供的数据类型
     */
    private String dataType;
    /**
     * 对于日期类型的列的分数秒精度
     */
    private Integer datetimePrecision;
    /**
     * <p>该列用于显示额外的信息
     * <p>auto_increment: 代表该列有AUTO_INCREMENT属性
     * <p>on update: 对于TIMESTAMP 或 DATETIME类型的列,CURRENT_TIMESTAMP有ON UPDATE CURRENT_TIMESTAMP属性
     * <p>VIRTUAL GENERATED 或者 VIRTUAL STORED 对于生成列的一些信息
     */
    private String extra;

    public boolean isAutoIncrement() {
        return extra != null && extra.contains("auto_increment");
    }

    /**
     * 如果是生成列，这里显示用来继续其值的表达式，否则为空
     */
    private String generationExpression;
    /**
     * 该列是否为空(YES/NO)
     */
    private String isNullable;

    public boolean isNotNull() {
        return "NO".equals(isNullable);
    }

    /**
     * number类型的列的精度。否则，返回 null
     */
    private Long numericPrecision;
    /**
     * number类型的列的scale。否则，返回 null
     */
    private Long numericScale;
    /**
     * 该列在表中的位置
     */
    private Integer ordinalPosition;
    /**
     * 你对该列所拥有的权限
     */
    private String privileges;
    /**
     * 不知道
     */
    private Integer srsId;
    /**
     * 包含列的表所属的目录的名称，该值总是def
     */
    private String tableCatalog;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表所有者（对于schema的名称）
     */
    private String tableSchema;
}
