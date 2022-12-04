package com.tangzc.mpe.autotable.strategy.sqlite.data;

import com.tangzc.mpe.autotable.strategy.TableMetadata;
import lombok.Data;

/**
 * @author don
 */
@Data
public class SqliteTableMetadata implements TableMetadata {

    private String tableName;
}
