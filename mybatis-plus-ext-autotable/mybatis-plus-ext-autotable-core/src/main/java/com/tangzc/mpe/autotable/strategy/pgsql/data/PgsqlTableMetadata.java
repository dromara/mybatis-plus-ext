package com.tangzc.mpe.autotable.strategy.pgsql.data;

import com.tangzc.mpe.autotable.strategy.TableMetadata;
import lombok.Data;

import java.util.List;

/**
 * @author don
 */
@Data
public class PgsqlTableMetadata implements TableMetadata {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 注释
     */
    private String comment;

    /**
     * 所有列信息
     */
    private List<PgsqlColumnMetadata> columnMetadataList;

    /**
     * 所有索引信息
     */
    private List<PgsqlIndexMetadata> indexMetadataList;
}
