package com.tangzc.mpe.autotable.strategy.mysql.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaColumn;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaStatistics;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaTable;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 创建更新表结构的Mapper
 * @author don
 */
@InterceptorIgnore(tenantLine = "true", illegalSql = "true", blockAttack = "true")
public interface MysqlTablesMapper {

    /**
     * 万能sql执行器
     *
     * @param sql 待执行的sql
     */
    @Select("${sql}")
    void executeSql(String sql);

    /**
     * 根据表名查询表在库中是否存在
     *
     * @param tableName 表结构的map
     * @return InformationSchemaTable
     */
    @Results({
            @Result(column = "table_catalog", property = "tableCatalog"),
            @Result(column = "table_schema", property = "tableSchema"),
            @Result(column = "table_name", property = "tableName"),
            @Result(column = "table_type", property = "tableType"),
            @Result(column = "engine", property = "engine"),
            @Result(column = "version", property = "version"),
            @Result(column = "row_format", property = "rowFormat"),
            @Result(column = "table_rows", property = "tableRows"),
            @Result(column = "avg_row_length", property = "avgRowLength"),
            @Result(column = "data_length", property = "dataLength"),
            @Result(column = "max_data_length", property = "maxDataLength"),
            @Result(column = "index_length", property = "indexLength"),
            @Result(column = "data_free", property = "dataFree"),
            @Result(column = "auto_increment", property = "autoIncrement"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "check_time", property = "checkTime"),
            @Result(column = "table_collation", property = "tableCollation"),
            @Result(column = "checksum", property = "checksum"),
            @Result(column = "create_options", property = "createOptions"),
            @Result(column = "table_comment", property = "tableComment"),
    })
    @Select("select * from information_schema.tables where table_name = #{tableName} and table_schema = (select database())")
    InformationSchemaTable findTableByTableName(String tableName);

    /**
     * 根据表名查询库中该表的字段结构等信息
     *
     * @param tableName 表结构的map
     * @return 表的字段结构等信息
     */
    @Results({
            @Result(column = "character_maximum_length", property = "characterMaximumLength"),
            @Result(column = "character_octet_length", property = "characterOctetLength"),
            @Result(column = "character_set_name", property = "characterSetName"),
            @Result(column = "collation_name", property = "collationName"),
            @Result(column = "column_comment", property = "columnComment"),
            @Result(column = "column_default", property = "columnDefault"),
            @Result(column = "column_key", property = "columnKey"),
            @Result(column = "column_name", property = "columnName"),
            @Result(column = "column_type", property = "columnType"),
            @Result(column = "data_type", property = "dataType"),
            @Result(column = "datetime_precision", property = "datetimePrecision"),
            @Result(column = "extra", property = "extra"),
            @Result(column = "generation_expression", property = "generationExpression"),
            @Result(column = "is_nullable", property = "isNullable"),
            @Result(column = "numeric_precision", property = "numericPrecision"),
            @Result(column = "numeric_scale", property = "numericScale"),
            @Result(column = "ordinal_position", property = "ordinalPosition"),
            @Result(column = "privileges", property = "privileges"),
            @Result(column = "srs_id", property = "srsId"),
            @Result(column = "table_catalog", property = "tableCatalog"),
            @Result(column = "table_name", property = "tableName"),
            @Result(column = "table_schema", property = "tableSchema"),
    })
    @Select("select * from information_schema.columns where table_name = #{tableName} and table_schema = (select database()) order by ordinal_position asc")
    List<InformationSchemaColumn> findTableEnsembleByTableName(String tableName);

    /**
     * 查询指定表的所有主键和索引信息
     *
     * @param tableName 表名
     * @return 所有主键和索引信息
     */
    @Results({
            @Result(column = "cardinality", property = "cardinality"),
            @Result(column = "collation", property = "collation"),
            @Result(column = "column_name", property = "columnName"),
            @Result(column = "comment", property = "comment"),
            @Result(column = "expression", property = "expression"),
            @Result(column = "index_comment", property = "indexComment"),
            @Result(column = "index_name", property = "indexName"),
            @Result(column = "index_schema", property = "indexSchema"),
            @Result(column = "index_type", property = "indexType"),
            @Result(column = "is_visible", property = "isVisible"),
            @Result(column = "non_unique", property = "nonUnique"),
            @Result(column = "nullable", property = "nullable"),
            @Result(column = "packed", property = "packed"),
            @Result(column = "seq_in_index", property = "seqInIndex"),
            @Result(column = "sub_part", property = "subPart"),
            @Result(column = "table_catalog", property = "tableCatalog"),
            @Result(column = "table_name", property = "tableName"),
            @Result(column = "table_schema", property = "tableSchema"),
    })
    @Select("SELECT * FROM information_schema.statistics WHERE table_name = #{tableName} and table_schema = (select database())")
    List<InformationSchemaStatistics> queryTablePrimaryAndIndex(String tableName);

    /**
     * 根据表名删除表
     *
     * @param tableName 表名
     */
    @Delete("DROP TABLE IF EXISTS `${tableName}`")
    void dropTableByName(String tableName);
}
