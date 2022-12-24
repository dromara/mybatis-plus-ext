package com.tangzc.mpe.autotable.strategy.pgsql;

import com.tangzc.mpe.autotable.constants.DatabaseDialect;
import com.tangzc.mpe.autotable.strategy.IStrategy;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlCompareTableInfo;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlTableMetadata;

/**
 * @author don
 */
public class PgsqlStrategy implements IStrategy<PgsqlTableMetadata, PgsqlCompareTableInfo> {

    @Override
    public DatabaseDialect dbDialect() {
        return DatabaseDialect.PostgreSQL;
    }

    @Override
    public void dropTable(String tableName) {

    }

    @Override
    public boolean checkTableExist(String tableName) {
        return false;
    }

    @Override
    public PgsqlTableMetadata analyseClass(Class<?> beanClass) {
        return null;
    }

    @Override
    public void createTable(PgsqlTableMetadata tableMetadata) {

    }

    @Override
    public PgsqlCompareTableInfo compareTable(PgsqlTableMetadata tableMetadata) {
        return null;
    }

    @Override
    public void modifyTable(PgsqlCompareTableInfo pgsqlCompareTableInfo) {

    }
}
