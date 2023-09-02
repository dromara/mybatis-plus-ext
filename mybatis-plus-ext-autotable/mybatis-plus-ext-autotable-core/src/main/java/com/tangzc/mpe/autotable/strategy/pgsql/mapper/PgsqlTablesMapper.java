package com.tangzc.mpe.autotable.strategy.pgsql.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.tangzc.mpe.autotable.strategy.pgsql.data.dbdata.PgsqlDbColumn;
import com.tangzc.mpe.autotable.strategy.pgsql.data.dbdata.PgsqlDbIndex;
import com.tangzc.mpe.autotable.strategy.pgsql.data.dbdata.PgsqlDbPrimary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 创建更新表结构的Mapper
 *
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

    /**
     * 注意使用${}占位，而不是#{}，是因为表名前面没有=这种公式，会导致解析后的表名有问题
     *
     * @param tableName 表名
     */
    @Delete("DROP TABLE IF EXISTS ${tableName}")
    void dropTableByName(String tableName);

    /**
     * 判断表是否存在
     *
     * @param tableName 表名
     * @return 数量
     */
    @Select("select count(*) from pg_class where relname = #{tableName}")
    int checkTableExist(String tableName);

    /**
     * 查询表名注释
     *
     * @param tableName 表名
     * @return 表注释
     */
    @Select("select description from pg_description where objoid=(select oid from pg_class where relname = #{tableName}) and objsubid=0")
    String selectTableDescription(String tableName);

    /**
     * 查询所有字段信息
     *
     * @param tableName 表名
     * @return 字段信息
     */
    @Results({
            @Result(column = "primary", property = "primary"),
            @Result(column = "description", property = "description"),
            @Result(column = "table_catalog", property = "tableCatalog"),
            @Result(column = "table_schema", property = "tableSchema"),
            @Result(column = "table_name", property = "tableName"),
            @Result(column = "column_name", property = "columnName"),
            @Result(column = "ordinal_position", property = "ordinalPosition"),
            @Result(column = "column_default", property = "columnDefault"),
            @Result(column = "is_nullable", property = "isNullable"),
            @Result(column = "data_type", property = "dataType"),
            @Result(column = "character_maximum_length", property = "characterMaximumLength"),
            @Result(column = "character_octet_length", property = "characterOctetLength"),
            @Result(column = "numeric_precision", property = "numericPrecision"),
            @Result(column = "numeric_precision_radix", property = "numericPrecisionRadix"),
            @Result(column = "numeric_scale", property = "numericScale"),
            @Result(column = "datetime_precision", property = "datetimePrecision"),
            @Result(column = "interval_type", property = "intervalType"),
            @Result(column = "interval_precision", property = "intervalPrecision"),
            @Result(column = "character_set_catalog", property = "characterSetCatalog"),
            @Result(column = "character_set_schema", property = "characterSetSchema"),
            @Result(column = "character_set_name", property = "characterSetName"),
            @Result(column = "collation_catalog", property = "collationCatalog"),
            @Result(column = "collation_schema", property = "collationSchema"),
            @Result(column = "collation_name", property = "collationName"),
            @Result(column = "domain_catalog", property = "domainCatalog"),
            @Result(column = "domain_schema", property = "domainSchema"),
            @Result(column = "domain_name", property = "domainName"),
            @Result(column = "udt_catalog", property = "udtCatalog"),
            @Result(column = "udt_schema", property = "udtSchema"),
            @Result(column = "udt_name", property = "udtName"),
            @Result(column = "scope_catalog", property = "scopeCatalog"),
            @Result(column = "scope_schema", property = "scopeSchema"),
            @Result(column = "scope_name", property = "scopeName"),
            @Result(column = "maximum_cardinality", property = "maximumCardinality"),
            @Result(column = "dtd_identifier", property = "dtdIdentifier"),
            @Result(column = "is_self_referencing", property = "isSelfReferencing"),
            @Result(column = "is_identity", property = "isIdentity"),
            @Result(column = "identity_generation", property = "identityGeneration"),
            @Result(column = "identity_start", property = "identityStart"),
            @Result(column = "identity_increment", property = "identityIncrement"),
            @Result(column = "identity_maximum", property = "identityMaximum"),
            @Result(column = "identity_minimum", property = "identityMinimum"),
            @Result(column = "identity_cycle", property = "identityCycle"),
            @Result(column = "is_generated", property = "isGenerated"),
            @Result(column = "generation_expression", property = "generationExpression"),
            @Result(column = "is_updatable", property = "isUpdatable"),
    })
    @Select("SELECT key_col.column_name is not null as primary, des.description, col.* " +
            "FROM information_schema.columns col " +
            "LEFT JOIN pg_description des on col.ordinal_position=des.objsubid and col.table_name::regclass=des.objoid " +
            "LEFT JOIN information_schema.key_column_usage key_col on key_col.column_name = col.column_name and key_col.table_name = col.table_name " +
            "WHERE col.table_name = #{tableName}")
    List<PgsqlDbColumn> selectTableFieldDetail(String tableName);

    /**
     * 查询所有索引信息
     *
     * @param tableName 表名
     * @return 索引信息
     */
    @Results({
            @Result(column = "description", property = "description"),
            @Result(column = "schemaname", property = "schemaName"),
            @Result(column = "tablename", property = "tableName"),
            @Result(column = "indexname", property = "indexName"),
            @Result(column = "tablespace", property = "tablespace"),
            @Result(column = "indexdef", property = "indexdef"),
    })
    @Select("SELECT d.description,idx.* FROM pg_indexes idx LEFT JOIN pg_description d ON idx.indexname::regclass = d.objoid WHERE idx.tablename = #{tableName}")
    List<PgsqlDbIndex> selectTableIndexesDetail(String tableName);

    /**
     * 查询表下的主键信息
     *
     * @param tableName 表明
     * @return 主键名
     */
    @Results({
            @Result(column = "primary_name", property = "primaryName"),
            @Result(column = "columns", property = "columns"),
    })
    @Select("SELECT constraint_name as primary_name, string_agg(column_name, ',' ORDER BY ordinal_position ASC) as columns " +
            "FROM information_schema.key_column_usage " +
            "WHERE table_name = #{tableName} GROUP BY constraint_name limit 1")
    PgsqlDbPrimary selectPrimaryKeyName(String tableName);
}
