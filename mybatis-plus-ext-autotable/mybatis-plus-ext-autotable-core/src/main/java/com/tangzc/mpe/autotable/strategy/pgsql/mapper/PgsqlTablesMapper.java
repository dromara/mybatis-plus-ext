package com.tangzc.mpe.autotable.strategy.pgsql.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaColumn;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaStatistics;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaTable;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;


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
    void executeSelect(String sql);
}
