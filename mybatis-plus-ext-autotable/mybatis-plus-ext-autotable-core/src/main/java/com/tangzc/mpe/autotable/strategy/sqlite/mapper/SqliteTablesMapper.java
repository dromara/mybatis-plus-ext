package com.tangzc.mpe.autotable.strategy.sqlite.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.tangzc.mpe.autotable.strategy.sqlite.data.dbdata.SqliteMaster;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 创建更新表结构的Mapper
 * @author don
 */
@InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
public interface SqliteTablesMapper {

    /**
     * 万能sql执行器
     *
     * @param sql 待执行的sql
     */
    @Select("${sql}")
    void executeSelect(String sql);

    /**
     * 根据表名删除表
     *
     * @param tableName 表名
     */
    @Delete("drop table if exists `${tableName}`;")
    void dropTableByName(String tableName);

    /**
     * 根据表名判断是否存在
     *
     * @param tableName 表名
     * @return 影响行数
     */
    @Select("select count(1) from `sqlite_master` where type='table' and name=#{tableName};")
    int checkTableExist(String tableName);

    /**
     * 查询建表语句
     *
     * @param tableName 表名
     * @return 建表语句
     */
    @Select("select `sql` from sqlite_master where type='table' and name=#{tableName};")
    String queryBuildTableSql(String tableName);

    /**
     * 查询建表语句
     *
     * @param tableName 表名
     * @return 建表语句
     */
    @Select("select * from sqlite_master where type='index' and tbl_name=#{tableName};")
    List<SqliteMaster> queryBuildIndexSql(String tableName);

    /**
     * 删除索引
     *
     * @param indexName 索引名
     */
    @Delete("drop index if exists \"${indexName}\";")
    void dropIndexSql(String indexName);

    /**
     * 备份表
     *
     * @param orgTableName 原表名称
     * @param backupTableName 备份的名称
     */
    @Select("ALTER TABLE \"${orgTableName}\" RENAME TO \"${backupTableName}\";")
    void backupTable(String orgTableName, String backupTableName);

    /**
     * 备份表
     *
     * @param orgTableName 原表名称
     * @param backupTableName 备份的名称
     */
    @Insert("INSERT INTO \"${orgTableName}\" SELECT * FROM \"${backupTableName}\";")
    void migrateData(String orgTableName, String backupTableName);
}
