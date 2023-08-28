package com.tangzc.mpe.autotable.strategy.pgsql.data.dbdata;

import lombok.Data;

/**
 * pgsql数据库，索引信息
 */
@Data
public class PgsqlDbIndex {

    /**
     * 索引注释
     */
    private String description;
    /**
     * 索引所属的模式（命名空间）名称。
     */
    private String schemaName;
    /**
     * 索引所属的表名称。
     */
    private String tableName;
    /**
     * 索引名
     */
    private String indexName;
    /**
     *
     */
    private String tablespace;
    /**
     * 索引创建语句
     */
    private String indexdef;
}
