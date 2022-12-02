package com.tangzc.mpe.autotable.strategy.mysql.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaStatistics;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaTable;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaColumn;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 创建更新表结构的Mapper
 */
@InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
public interface MysqlTablesMapper {

    /**
     * 万能sql执行器
     *
     * @param sql 待执行的sql
     */
    @Select("${sql}")
    void executeSelect(String sql);

    /**
     * 根据表名查询表在库中是否存在
     *
     * @param tableName 表结构的map
     * @return InformationSchemaTable
     */
    @Select("select * from information_schema.tables where table_name = #{tableName} and table_schema = (select database())")
    InformationSchemaTable findTableByTableName(String tableName);

    /**
     * 根据表名查询库中该表的字段结构等信息
     *
     * @param tableName 表结构的map
     * @return 表的字段结构等信息
     */
    @Select("select * from information_schema.columns where table_name = #{tableName} and table_schema = (select database())")
    List<InformationSchemaColumn> findTableEnsembleByTableName(String tableName);

    /**
     * 查询指定表的所有主键和索引信息
     *
     * @param tableName 表名
     */
    @Select("SELECT * FROM information_schema.statistics WHERE table_name = #{tableName} and table_schema = (select database())")
    List<InformationSchemaStatistics> queryTablePrimaryAndIndex(String tableName);

    /**
     * 根据表名删除表
     *
     * @param tableName 表名
     */
    @Select("DROP TABLE IF EXISTS `${tableName}`")
    void dropTableByName(String tableName);
}
