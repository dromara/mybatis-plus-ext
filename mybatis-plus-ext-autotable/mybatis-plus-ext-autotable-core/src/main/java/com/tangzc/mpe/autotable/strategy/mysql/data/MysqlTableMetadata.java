package com.tangzc.mpe.autotable.strategy.mysql.data;

import com.tangzc.mpe.autotable.strategy.TableMetadata;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author don
 */
@Data
public class MysqlTableMetadata implements TableMetadata {

    /**
     * 表名
     */
    private String tableName;
    /**
     * 引擎
     */
    private String engine;
    /**
     * 默认字符集
     */
    private String characterSet;
    /**
     * 默认排序规则
     */
    private String collate;
    /**
     * 注释
     */
    private String comment;
    /**
     * 所有列
     */
    private List<MysqlColumnMetadata> columnMetadataList = new ArrayList<>();
    /**
     * 索引
     */
    private List<MysqlIndexMetadata> indexMetadataList = new ArrayList<>();
}
