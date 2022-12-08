package com.tangzc.mpe.autotable.strategy.sqlite.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

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
}
