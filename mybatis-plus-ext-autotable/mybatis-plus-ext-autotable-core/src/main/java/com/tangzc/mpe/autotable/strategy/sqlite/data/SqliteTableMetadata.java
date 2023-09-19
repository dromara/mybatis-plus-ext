package com.tangzc.mpe.autotable.strategy.sqlite.data;

import com.tangzc.mpe.autotable.strategy.TableMetadata;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author don
 */
@Data
public class SqliteTableMetadata implements TableMetadata {

    /**
     * 表名
     */
    private String tableName;
    /**
     * 注释
     */
    private String comment;
    /**
     * 所有列
     */
    private List<SqliteColumnMetadata> columnMetadataList = new ArrayList<>();
    /**
     * 索引
     */
    private List<SqliteIndexMetadata> indexMetadataList = new ArrayList<>();
}
