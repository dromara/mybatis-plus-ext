package com.tangzc.mpe.autotable.strategy.sqlite;

import com.tangzc.mpe.autotable.constants.DatabaseDialect;
import com.tangzc.mpe.autotable.strategy.IStrategy;
import com.tangzc.mpe.autotable.strategy.sqlite.builder.TableMetadataBuilder;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteCompareTableInfo;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteIndexMetadata;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteTableMetadata;
import com.tangzc.mpe.autotable.strategy.sqlite.builder.CreateTableSqlBuilder;
import com.tangzc.mpe.autotable.strategy.sqlite.data.dbdata.SqliteMaster;
import com.tangzc.mpe.autotable.strategy.sqlite.mapper.SqliteTablesMapper;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author don
 */
public class SqliteStrategy implements IStrategy<SqliteTableMetadata, SqliteCompareTableInfo> {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

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
        String createTableSql = CreateTableSqlBuilder.buildTableSql(tableMetadata.getTableName(), tableMetadata.getComment(), tableMetadata.getColumnMetadataList());
        sqliteTablesMapper.executeSelect(createTableSql);
        List<String> createIndexSqlList = CreateTableSqlBuilder.buildIndexSql(tableMetadata.getTableName(), tableMetadata.getIndexMetadataList());
        for (String createIndexSql : createIndexSqlList) {
            sqliteTablesMapper.executeSelect(createIndexSql);
        }
    }

    @Override
    public SqliteCompareTableInfo compareTable(SqliteTableMetadata tableMetadata) {

        String tableName = tableMetadata.getTableName();
        SqliteCompareTableInfo sqliteCompareTableInfo = new SqliteCompareTableInfo(tableName);

        // 判断表是否需要重建
        String orgBuildTableSql = sqliteTablesMapper.queryBuildTableSql(tableName);
        String newBuildTableSql = CreateTableSqlBuilder.buildTableSql(tableMetadata.getTableName(), tableMetadata.getComment(), tableMetadata.getColumnMetadataList());
        boolean needRebuildTable = !Objects.equals(orgBuildTableSql + ";", newBuildTableSql);
        if (needRebuildTable) {
            // 该情况下无需单独分析索引了，因为sqlite的表修改方式为重建整个表，索引需要全部删除，重新创建
            sqliteCompareTableInfo.setRebuildTableSql(newBuildTableSql);
            // 删除当前所有索引
            List<SqliteMaster> orgBuildIndexSqlList = sqliteTablesMapper.queryBuildIndexSql(tableName);
            for (SqliteMaster sqliteMaster : orgBuildIndexSqlList) {
                sqliteCompareTableInfo.getDeleteIndexList().add(sqliteMaster.getName());
            }
            // 添加新建索引的sql
            List<String> buildIndexSqlList = CreateTableSqlBuilder.buildIndexSql(tableName, tableMetadata.getIndexMetadataList());
            for (String buildIndexSql : buildIndexSqlList) {
                sqliteCompareTableInfo.getBuildIndexSqlList().add(buildIndexSql);
            }
        } else {
            // 不需要重建表的情况下，才有必要单独判断索引的更新情况
            // 判断索引是否需要重建 <索引name，索引sql>
            Map<String, String> rebuildIndexMap = tableMetadata.getIndexMetadataList().stream()
                    .collect(Collectors.toMap(
                            SqliteIndexMetadata::getName,
                            indexMetadata -> CreateTableSqlBuilder.getIndexSql(tableName, indexMetadata)
                    ));
            // 遍历所有数据库存在的索引，判断有没有变化
            List<SqliteMaster> orgBuildIndexSqlList = sqliteTablesMapper.queryBuildIndexSql(tableName);
            for (SqliteMaster sqliteMaster : orgBuildIndexSqlList) {
                String indexName = sqliteMaster.getName();
                String newBuildIndexSql = rebuildIndexMap.remove(indexName);
                boolean exit = newBuildIndexSql != null;
                // 如果最新构建标记上没有该注解的标记了，则说明该注解需要删除了
                if (!exit) {
                    sqliteCompareTableInfo.getDeleteIndexList().add(indexName);
                }
                // 新的索引构建语句中存在相同名称的索引，且内容不一致，需要重新构建
                String createIndexSqlRecord = sqliteMaster.getSql() + ";";
                if (exit && !Objects.equals(newBuildIndexSql, createIndexSqlRecord)) {
                    sqliteCompareTableInfo.getDeleteIndexList().add(indexName);
                    sqliteCompareTableInfo.getBuildIndexSqlList().add(newBuildIndexSql);
                }
            }
            // 筛选完，剩下的，是需要新增的索引
            Map<String, String> needNewIndexes = rebuildIndexMap;
            if (!needNewIndexes.isEmpty()) {
                sqliteCompareTableInfo.getBuildIndexSqlList().addAll(needNewIndexes.values());
            }
        }

        return sqliteCompareTableInfo;
    }

    @Override
    public void modifyTable(SqliteCompareTableInfo sqliteCompareTableInfo) {

        // 删除索引
        List<String> deleteIndexList = sqliteCompareTableInfo.getDeleteIndexList();
        if (!deleteIndexList.isEmpty()) {
            for (String deleteIndexName : deleteIndexList) {
                sqliteTablesMapper.dropIndexSql(deleteIndexName);
            }
        }

        // 重建表
        String rebuildTableSql = sqliteCompareTableInfo.getRebuildTableSql();
        if (StringUtils.hasText(rebuildTableSql)) {
            String orgTableName = sqliteCompareTableInfo.getName();
            String backupTableName = getBackupTableName(orgTableName);
            // 备份表
            sqliteTablesMapper.backupTable(orgTableName, backupTableName);
            // 重新建表
            sqliteTablesMapper.executeSelect(rebuildTableSql);
            // 迁移数据
            sqliteTablesMapper.migrateData(orgTableName, backupTableName);
        }

        // 创建索引
        List<String> buildIndexSqlList = sqliteCompareTableInfo.getBuildIndexSqlList();
        if (!buildIndexSqlList.isEmpty()) {
            for (String buildIndexSql : buildIndexSqlList) {
                sqliteTablesMapper.executeSelect(buildIndexSql);
            }
        }
    }

    private String getBackupTableName(String orgTableName) {

        int offset = 0;
        String backupName = "_" + orgTableName + "_old_" + LocalDateTime.now().format(dateTimeFormatter);
        while (true) {
            if (offset > 0) {
                backupName += "_" + offset;
            }
            int count = sqliteTablesMapper.checkTableExist(backupName);
            if (count == 0) {
                return backupName;
            } else {
                offset++;
            }
        }
    }
}
