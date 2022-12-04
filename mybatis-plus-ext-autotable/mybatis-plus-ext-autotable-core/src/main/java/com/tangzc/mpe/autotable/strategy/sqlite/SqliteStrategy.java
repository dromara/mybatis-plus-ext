package com.tangzc.mpe.autotable.strategy.sqlite;

import com.tangzc.mpe.autotable.constants.DatabaseDialect;
import com.tangzc.mpe.autotable.strategy.IStrategy;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteCompareTableInfo;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteTableMetadata;

/**
 * @author don
 */
public class SqliteStrategy implements IStrategy<SqliteTableMetadata, SqliteCompareTableInfo> {
    @Override
    public DatabaseDialect dbDialect() {
        return DatabaseDialect.SQLite;
    }

    @Override
    public void dropTable(String tableName) {

    }

    @Override
    public boolean checkTableExist(String tableName) {
        return false;
    }

    @Override
    public SqliteTableMetadata analyseClass(Class<?> beanClass) {
        return new SqliteTableMetadata();
    }

    @Override
    public void createTable(SqliteTableMetadata tableMetadata) {
        System.out.println("创建sqlite表");
    }

    @Override
    public SqliteCompareTableInfo compareTable(SqliteTableMetadata tableMetadata) {
        return new SqliteCompareTableInfo();
    }

    @Override
    public void modifyTable(SqliteCompareTableInfo sqliteCompareTableInfo) {
        System.out.println("修改sqlite表");
    }
}
