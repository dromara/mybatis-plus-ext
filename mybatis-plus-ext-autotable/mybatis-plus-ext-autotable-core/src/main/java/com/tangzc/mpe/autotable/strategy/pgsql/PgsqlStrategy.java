package com.tangzc.mpe.autotable.strategy.pgsql;

import com.tangzc.mpe.autotable.constants.DatabaseDialect;
import com.tangzc.mpe.autotable.strategy.IStrategy;
import com.tangzc.mpe.autotable.strategy.pgsql.builder.CreateTableSqlBuilder;
import com.tangzc.mpe.autotable.strategy.pgsql.builder.TableMetadataBuilder;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlCompareTableInfo;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlTableMetadata;
import com.tangzc.mpe.autotable.strategy.pgsql.mapper.PgsqlTablesMapper;

import javax.annotation.Resource;

/**
 * @author don
 */
public class PgsqlStrategy implements IStrategy<PgsqlTableMetadata, PgsqlCompareTableInfo> {

    @Resource
    private PgsqlTablesMapper pgsqlTablesMapper;

    @Override
    public DatabaseDialect dbDialect() {
        return DatabaseDialect.PostgreSQL;
    }

    @Override
    public void dropTable(String tableName) {
        pgsqlTablesMapper.dropTableByName(tableName);
    }

    @Override
    public boolean checkTableExist(String tableName) {
        return pgsqlTablesMapper.checkTableExist(tableName) > 0;
    }

    @Override
    public PgsqlTableMetadata analyseClass(Class<?> beanClass) {
        PgsqlTableMetadata pgsqlTableMetadata = TableMetadataBuilder.build(beanClass);
        if (pgsqlTableMetadata.getColumnMetadataList().isEmpty()) {
            log.warn("扫描发现{}没有建表字段请检查！", beanClass.getName());
            return null;
        }
        return pgsqlTableMetadata;
    }

    @Override
    public void createTable(PgsqlTableMetadata tableMetadata) {
        String buildSql = CreateTableSqlBuilder.buildSql(tableMetadata);
        log.info("执行SQL：{}", buildSql);
        pgsqlTablesMapper.executeSql(buildSql);
    }

    @Override
    public PgsqlCompareTableInfo compareTable(PgsqlTableMetadata tableMetadata) {
        // TODO
        return null;
    }

    @Override
    public void modifyTable(PgsqlCompareTableInfo pgsqlCompareTableInfo) {
        // TODO
    }
}
