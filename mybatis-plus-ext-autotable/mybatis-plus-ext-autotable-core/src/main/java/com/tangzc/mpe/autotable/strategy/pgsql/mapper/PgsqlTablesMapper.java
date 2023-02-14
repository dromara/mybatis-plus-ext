package com.tangzc.mpe.autotable.strategy.pgsql.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;


/**
 * 创建更新表结构的Mapper
 * @author don
 */
@InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
public interface PgsqlTablesMapper {

    /**
     * 万能sql执行器
     *
     * @param sql 待执行的sql
     */
    @Select("${sql}")
    void executeSql(String sql);

    @Delete("DROP TABLE IF EXISTS #{tableName}")
    void dropTableByName(String tableName);

    @Delete("select count(*) from pg_class where relname = #{tableName}")
    int checkTableExist(String tableName);
}
