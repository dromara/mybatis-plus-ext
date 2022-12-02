package com.tangzc.mpe.autotable.strategy.mysql.data.dbdata;

import lombok.Data;

/**
 * 数据库表主键以及索引的信息
 * @author don
 */
@Data
public class InformationSchemaStatistics {

    /**
     * 估计索引中唯一值的数量，该值不一定精确
     */
    private Long cardinality;
    /**
     * 列在索引中排序方式：A（升序），D（降序），NULL（未排序）
     */
    private String collation;
    /**
     * 列名称
     */
    private String columnName;
    /**
     * 有关未在其自己的列中描述的索引的信息，例如在禁用索引时禁用
     */
    private String comment;
    private String expression;
    /**
     * 索引注释
     */
    private String indexComment;
    /**
     * 索引的名称。 如果是主键，则始终为PRIMARY
     */
    private String indexName;
    /**
     * 索引所属的结构（数据库）的名称
     */
    private String indexSchema;
    /**
     * 索引类型（BTREE，FULLTEXT，HASH，RTREE）
     */
    private String indexType;
    private String isVisible;
    /**
     * 如果索引不能重复，则为0;如果可以，则为1
     */
    private Integer nonUnique;
    /**
     * 如果列可能包含NULL值，则包含YES，否则包含’’
     */
    private String nullable;
    /**
     * 指示密钥的打包方式。 如果不是，则为NULL
     */
    private String packed;
    /**
     * 索引中的列序列号，以1开头
     */
    private Integer seqInIndex;
    /**
     * 索引前缀。 如果列仅被部分索引，则索引字符的数量，如果整列被索引，则为NULL
     */
    private Long subPart;
    /**
     * 包含索引的表所属的目录的名称。 该值始终为def
     */
    private String tableCatalog;
    /**
     * 包含索引的表的名称
     */
    private String tableName;
    /**
     * 包含索引的表所属的结构（数据库）的名称
     */
    private String tableSchema;
}
