package com.tangzc.mpe.actable.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.tangzc.mpe.actable.command.SysMysqlColumns;
import com.tangzc.mpe.actable.command.SysMysqlTable;
import com.tangzc.mpe.actable.command.TableConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 创建更新表结构的Mapper
 *
 * @author sunchenbin
 */
@InterceptorIgnore(tenantLine = "true")
public interface CreateMysqlTablesMapper {

    /**
     * 根据结构注解解析出来的信息创建表
     *
     * @param tableMap 表结构的map
     */
    void createTable(@Param("tableMap") Map<String, TableConfig> tableMap);

    /**
     * 根据表名查询表在库中是否存在
     *
     * @param tableName 表结构的map
     * @return SysMysqlTable
     */
    SysMysqlTable findTableByTableName(@Param("tableName") String tableName);

    /**
     * 根据表名查询库中该表的字段结构等信息
     *
     * @param tableName 表结构的map
     * @return 表的字段结构等信息
     */
    List<SysMysqlColumns> findTableEnsembleByTableName(@Param("tableName") String tableName);

    /**
     * 增加字段
     *
     * @param tableMap 表结构的map
     */
    void addTableField(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 删除字段
     *
     * @param tableMap 表结构的map
     */
    void removeTableField(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 更新表属性
     *
     * @param tableMap 表结构的map
     */
    void modifyTableProperty(@Param("tableMap") Map<String, TableConfig> tableMap);

    /**
     * 修改字段
     *
     * @param tableMap 表结构的map
     */
    void modifyTableField(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 删除主键约束，附带修改其他字段属性功能
     *
     * @param tableMap 表结构的map
     */
    void dropKeyTableField(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 根据表名删除表
     *
     * @param tableName 表名
     */
    void dropTableByName(@Param("tableName") String tableName);

    /**
     * 查询当前表存在的索引(除了主键索引primary)
     *
     * @param tableMap 表名
     * @return 索引名列表
     */
    Set<String> findTableIndexByTableName(@Param("tableMap") Map<String, String> tableMap);

    /**
     * 删除表索引
     *
     * @param tableMap
     */
    void dropTabelIndex(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 创建索引
     *
     * @param tableMap
     */
    void addTableIndex(@Param("tableMap") Map<String, Object> tableMap);

    /**
     * 创建唯一约束
     *
     * @param tableMap
     */
    void addTableUnique(@Param("tableMap") Map<String, Object> tableMap);

}
