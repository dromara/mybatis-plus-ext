package com.tangzc.mpe.autotable.strategy.pgsql.data;

import com.tangzc.mpe.autotable.strategy.TableMetadata;
import lombok.Data;

/**
 * @author don
 */
@Data
public class PgsqlTableMetadata implements TableMetadata {

    /**
     * 表名
     */
    private String tableName;
}
