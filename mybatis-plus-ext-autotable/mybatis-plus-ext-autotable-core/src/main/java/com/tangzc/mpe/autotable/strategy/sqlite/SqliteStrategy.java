package com.tangzc.mpe.autotable.strategy.sqlite;

import com.tangzc.mpe.autotable.constants.DatabaseDialect;
import com.tangzc.mpe.autotable.strategy.IStrategy;
import com.tangzc.mpe.autotable.strategy.sqlite.builder.TableMetadataBuilder;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteCompareTableInfo;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteTableMetadata;
import com.tangzc.mpe.autotable.strategy.sqlite.data.dbdata.CreateTableSqlBuilder;
import com.tangzc.mpe.autotable.strategy.sqlite.mapper.SqliteTablesMapper;

import javax.annotation.Resource;

/**
 * @author don
 */
public class SqliteStrategy implements IStrategy<SqliteTableMetadata, SqliteCompareTableInfo> {

    @Resource
    private SqliteTablesMapper sqliteTablesMapper;

    @Override
    public DatabaseDialect dbDialect() {
        return DatabaseDialect.SQLite;
    }

    @Override
    public void dropTable(String tableName) {
        sqliteTablesMapper.dropTableByName(tableName);
    }

    @Override
    public boolean checkTableExist(String tableName) {
        int i = sqliteTablesMapper.checkTableExist(tableName);
        return i > 0;
    }

    @Override
    public SqliteTableMetadata analyseClass(Class<?> beanClass) {
        SqliteTableMetadata sqliteTableMetadata = TableMetadataBuilder.build(beanClass);
        if (sqliteTableMetadata.getColumnMetadataList().isEmpty()) {
            log.warn("扫描发现{}没有建表字段请检查！", beanClass.getName());
            return null;
        }
        return sqliteTableMetadata;
    }

    @Override
    public void createTable(SqliteTableMetadata tableMetadata) {
        String createTableSql = CreateTableSqlBuilder.buildSql(tableMetadata);
        sqliteTablesMapper.executeSelect(createTableSql);
    }

    @Override
    public SqliteCompareTableInfo compareTable(SqliteTableMetadata tableMetadata) {
        // TODO
        return new SqliteCompareTableInfo();
    }

    @Override
    public void modifyTable(SqliteCompareTableInfo sqliteCompareTableInfo) {
        // TODO
        System.out.println("修改sqlite表");
    }
}
