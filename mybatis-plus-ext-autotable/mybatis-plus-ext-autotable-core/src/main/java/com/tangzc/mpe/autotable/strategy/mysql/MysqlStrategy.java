package com.tangzc.mpe.autotable.strategy.mysql;

import com.google.common.base.Functions;
import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import com.tangzc.mpe.autotable.annotation.enums.IndexSortTypeEnum;
import com.tangzc.mpe.autotable.constants.DatabaseType;
import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import com.tangzc.mpe.autotable.strategy.IStrategy;
import com.tangzc.mpe.autotable.strategy.mysql.builder.CreateTableSqlBuilder;
import com.tangzc.mpe.autotable.strategy.mysql.builder.ModifyTableSqlBuilder;
import com.tangzc.mpe.autotable.strategy.mysql.builder.TableMetadataBuilder;
import com.tangzc.mpe.autotable.strategy.mysql.data.*;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaColumn;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaStatistics;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaTable;
import com.tangzc.mpe.autotable.strategy.mysql.mapper.MysqlTablesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目启动时自动扫描配置的目录中的model，根据配置的规则自动创建或更新表 该逻辑只适用于mysql，其他数据库尚且需要另外扩展，因为sql的语法不同
 *
 * @author sunchenbin, Spet
 * @version 2019/07/06
 */
@Slf4j
public class MysqlStrategy implements IStrategy<MysqlTableMetadata, MysqlCompareTableInfo, InformationSchemaTable> {

    @Resource
    private AutoTableProperties autoTableProperties;

    @Resource
    private TableMetadataBuilder tableMetadataBuilder;

    @Resource
    private MysqlTablesMapper mysqlTablesMapper;

    @Override
    public DatabaseType dbType() {
        return DatabaseType.mysql;
    }

    @Override
    public void dropTable(String tableName) {
        mysqlTablesMapper.dropTableByName(tableName);
    }

    @Override
    public InformationSchemaTable getTableInformationFromDb(String tableName) {
        return mysqlTablesMapper.findTableByTableName(tableName);
    }

    @Override
    public MysqlTableMetadata analyseClass(Class<?> beanClass) {
        MysqlTableMetadata mysqlTableMetadata = tableMetadataBuilder.build(beanClass);
        List<MysqlColumnMetadata> mysqlColumnMetadataList = mysqlTableMetadata.getMysqlColumnMetadataList();
        if (mysqlColumnMetadataList.isEmpty()) {
            log.warn("扫描发现{}没有建表字段请检查！", beanClass.getName());
            return null;
        }
        return mysqlTableMetadata;
    }

    @Override
    public void createTable(MysqlTableMetadata tableMetadata) {
        log.info("开始创建表：{}", tableMetadata.getTableName());
        String sqlStr = CreateTableSqlBuilder.buildSql(tableMetadata);
        log.info("执行SQL：{}", sqlStr);
        mysqlTablesMapper.executeSelect(sqlStr);
        // insertExecuteSqlLog(sqlStr);
        log.info("结束创建表：{}", tableMetadata.getTableName());
    }

    @Override
    public MysqlCompareTableInfo compareTable(MysqlTableMetadata tableMetadata, InformationSchemaTable informationSchemaTable) {

        String tableName = tableMetadata.getTableName();

        MysqlCompareTableInfo mysqlCompareTableInfo = new MysqlCompareTableInfo(tableName);

        // 对比表配置有无变化
        compareTableProperties(tableMetadata, informationSchemaTable, mysqlCompareTableInfo);

        // 开始比对列的变化: 新增、修改、删除
        compareColumns(tableMetadata, tableName, mysqlCompareTableInfo);

        // 开始比对 主键 和 索引 的变化
        List<InformationSchemaStatistics> informationSchemaStatistics = mysqlTablesMapper.queryTablePrimaryAndIndex(tableName);
        // 按照主键（固定值：PRIMARY）、索引名字，对所有列进行分组
        Map<String, List<InformationSchemaStatistics>> keyColumnGroupByName = informationSchemaStatistics.stream()
                .collect(Collectors.groupingBy(InformationSchemaStatistics::getIndexName));

        // 对比主键
        List<InformationSchemaStatistics> tablePrimaries = keyColumnGroupByName.remove("PRIMARY");
        comparePrimary(tableMetadata, mysqlCompareTableInfo, tablePrimaries);

        // 对比索引, informationSchemaKeyColumnUsages中剩余的都是索引数据了
        Map<String, List<InformationSchemaStatistics>> tableIndexes = keyColumnGroupByName;
        compareIndexes(tableMetadata, mysqlCompareTableInfo, tableIndexes);

        return mysqlCompareTableInfo;
    }

    @Override
    public void modifyTable(MysqlCompareTableInfo mysqlCompareTableInfo) {
        String sqlStr = ModifyTableSqlBuilder.buildSql(mysqlCompareTableInfo);
        if (StringUtils.hasText(sqlStr)) {
            log.info("开始修改表：{}", mysqlCompareTableInfo.getName());
            log.info("执行SQL：{}", sqlStr);
            mysqlTablesMapper.executeSelect(sqlStr);
            // insertExecuteSqlLog(sqlStr);
            log.info("结束修改表：{}", mysqlCompareTableInfo.getName());
        }
    }

    private void compareIndexes(MysqlTableMetadata mysqlTableMetadata, MysqlCompareTableInfo mysqlCompareTableInfo, Map<String, List<InformationSchemaStatistics>> tableIndexs) {
        // Bean上所有的索引
        List<MysqlIndexMetadata> mysqlIndexMetadataList = mysqlTableMetadata.getMysqlIndexMetadataList();
        // 以Bean上的索引开启循环，逐个匹配表上的索引
        for (MysqlIndexMetadata mysqlIndexMetadata : mysqlIndexMetadataList) {
            // 根据Bean上的索引名称获取表上的索引
            String indexName = mysqlIndexMetadata.getName();
            // 获取表上对应索引名称的所有列
            List<InformationSchemaStatistics> theIndexColumns = tableIndexs.remove(indexName);
            if (theIndexColumns == null) {
                // 表上不存在该索引，新增
                mysqlCompareTableInfo.getMysqlIndexMetadataList().add(mysqlIndexMetadata);
            } else {
                // 先把表上的该索引的所有字段，按照顺序排列
                theIndexColumns = theIndexColumns.stream()
                        .sorted(Comparator.comparing(InformationSchemaStatistics::getSeqInIndex))
                        .collect(Collectors.toList());
                // 获取Bean上该索引涉及的所有字段（按照字段顺序自然排序）
                List<MysqlIndexMetadata.IndexColumnParam> columns = mysqlIndexMetadata.getColumns();
                // 先初步按照索引牵扯的字段数量一不一样判断是不是需要更新索引
                if (theIndexColumns.size() != columns.size()) {
                    // 同名的索引，但是表上的字段数量跟Bean上指定的不一致，需要修改（先删除，再新增）
                    mysqlCompareTableInfo.getDropIndexList().add(indexName);
                    mysqlCompareTableInfo.getMysqlIndexMetadataList().add(mysqlIndexMetadata);
                } else {
                    // 牵扯的字段数目一致，再按顺序逐个比较每个位置的列名及其排序方式是否相同
                    for (int i = 0; i < theIndexColumns.size(); i++) {
                        InformationSchemaStatistics informationSchemaStatistics = theIndexColumns.get(i);
                        IndexSortTypeEnum indexSort = IndexSortTypeEnum.parseFromMysql(informationSchemaStatistics.getCollation());
                        MysqlIndexMetadata.IndexColumnParam indexColumnParam = columns.get(i);
                        IndexSortTypeEnum indexColumnParamSort = indexColumnParam.getSort();

                        // 名字不同即不同
                        boolean nameIsDiff = !informationSchemaStatistics.getColumnName().equals(indexColumnParam.getColumn());
                        // 类注解指定排序方式不为空的情况下，与库中的值不同即不同
                        boolean sortTypeIsDiff = indexColumnParamSort != null && indexColumnParamSort != indexSort;
                        if (nameIsDiff || sortTypeIsDiff) {
                            // 同名的索引，但是表上的字段数量跟Bean上指定的不一致，需要修改（先删除，再新增）
                            mysqlCompareTableInfo.getDropIndexList().add(indexName);
                            mysqlCompareTableInfo.getMysqlIndexMetadataList().add(mysqlIndexMetadata);
                            break;
                        }
                    }
                }
            }
        }
        // 因为上一步循环，在基于Bean上索引匹配上表中的索引后，就立即删除了表上对应的索引，所以剩下的索引都是Bean上没有声明的索引，需要根据配置判断，是否删掉多余的索引
        Set<String> needDropIndexes = tableIndexs.keySet();
        if (autoTableProperties.isAutoDropIndex() && !needDropIndexes.isEmpty()) {
            mysqlCompareTableInfo.getDropIndexList().addAll(needDropIndexes);
        }
    }

    private static void comparePrimary(MysqlTableMetadata mysqlTableMetadata, MysqlCompareTableInfo mysqlCompareTableInfo, List<InformationSchemaStatistics> tablePrimaries) {
        // 存在主键的话
        if (CollectionUtils.isEmpty(tablePrimaries)) {
            // 如果当前表不存在主键，则更新主键(如果Bean上有的话)
            mysqlCompareTableInfo.setResetPrimary(true);
        } else {
            // 获取当前Bean上指定的主键列表，顺序按照列的自然顺序排列
            List<MysqlColumnMetadata> primaries = mysqlTableMetadata.getMysqlColumnMetadataList().stream()
                    .filter(MysqlColumnMetadata::isPrimary)
                    .collect(Collectors.toList());
            if (tablePrimaries.size() != primaries.size()) {
                // 主键数量不一致，需要更新
                mysqlCompareTableInfo.setResetPrimary(true);
            } else {
                // 主键数量一致的情况下，逐个比对每个位置的列名
                // 先按照顺序排好数据库主键的顺序
                tablePrimaries = tablePrimaries.stream()
                        .sorted(Comparator.comparing(InformationSchemaStatistics::getSeqInIndex))
                        .collect(Collectors.toList());
                for (int i = 0; i < tablePrimaries.size(); i++) {
                    // 获取Bean对应的位置的主键比对
                    InformationSchemaStatistics tablePrimary = tablePrimaries.get(i);
                    if (!tablePrimary.getColumnName().equals(primaries.get(i).getName())) {
                        // 主键列中按顺序比较，存在顺序不一致的情况，需要更新
                        mysqlCompareTableInfo.setResetPrimary(true);
                        break;
                    }
                }
            }
        }
    }

    private void compareColumns(MysqlTableMetadata mysqlTableMetadata, String tableName, MysqlCompareTableInfo mysqlCompareTableInfo) {
        List<MysqlColumnMetadata> mysqlColumnMetadataList = mysqlTableMetadata.getMysqlColumnMetadataList();
        // 变形：《列名，MysqlColumnMetadata》
        Map<String, MysqlColumnMetadata> columnParamMap = mysqlColumnMetadataList.stream().collect(Collectors.toMap(MysqlColumnMetadata::getName, Functions.identity()));
        // 查询所有列数据
        List<InformationSchemaColumn> tableColumnList = mysqlTablesMapper.findTableEnsembleByTableName(tableName);
        for (InformationSchemaColumn informationSchemaColumn : tableColumnList) {
            String columnName = informationSchemaColumn.getColumnName();
            // 以数据库字段名，从当前Bean上取信息，获取到就从中剔除
            MysqlColumnMetadata mysqlColumnMetadata = columnParamMap.remove(columnName);
            if (mysqlColumnMetadata != null) {
                // 取到了，则进行字段配置的比对
                boolean commentChanged = isCommentChanged(informationSchemaColumn, mysqlColumnMetadata);
                boolean fieldTypeChanged = isFieldTypeChanged(informationSchemaColumn, mysqlColumnMetadata);
                boolean notNullChanged = mysqlColumnMetadata.isNotNull() != informationSchemaColumn.isNotNull();
                boolean fieldIsAutoIncrementChanged = mysqlColumnMetadata.isAutoIncrement() != informationSchemaColumn.isAutoIncrement();
                boolean defaultValueChanged = isDefaultValueChanged(informationSchemaColumn, mysqlColumnMetadata);
                if (commentChanged || fieldTypeChanged || notNullChanged || fieldIsAutoIncrementChanged || defaultValueChanged) {
                    // 任何一项有变化，则说明需要更新该字段
                    mysqlCompareTableInfo.getModifyMysqlColumnMetadataList().add(mysqlColumnMetadata);
                }
            } else {
                // 没有取到对应字段，说明库中存在的字段，Bean上不存在，根据配置，决定是否删除库上的多余字段
                if (autoTableProperties.isAutoDropColumn()) {
                    mysqlCompareTableInfo.getDropColumnList().add(columnName);
                }
            }
        }
        // 因为按照表中字段已经晒过一轮Bean上的字段了，同名可以取到的均删除了，剩下的都是表中字段不存在的，需要新增
        Collection<MysqlColumnMetadata> needNewColumns = columnParamMap.values();
        mysqlCompareTableInfo.getMysqlColumnMetadataList().addAll(needNewColumns);
    }

    private static boolean isDefaultValueChanged(InformationSchemaColumn informationSchemaColumn, MysqlColumnMetadata mysqlColumnMetadata) {
        String columnDefault = informationSchemaColumn.getColumnDefault();
        DefaultValueEnum defaultValueType = mysqlColumnMetadata.getDefaultValueType();
        if (DefaultValueEnum.isValid(defaultValueType)) {
            // 需要设置为null，但是数据库当前不是null
            if (defaultValueType == DefaultValueEnum.NULL) {
                return columnDefault != null;
            }
            // 需要设置为空字符串，但是数据库当前不是空字符串
            if (defaultValueType == DefaultValueEnum.EMPTY_STRING) {
                return !"".equals(columnDefault);
            }
        } else {
            // 自定义值 默认值对比
            TypeAndLength paramType = mysqlColumnMetadata.getType();
            String defaultValue = mysqlColumnMetadata.getDefaultValue();
            // 未设置有效默认值，直接返回未变更
            if (StringUtils.isEmpty(defaultValue)) {
                return false;
            }
            // 如果是数据库是bit类型，默认值是b'1' 或者 b'0' 的形式
            if (paramType.isBoolean() && columnDefault.startsWith("b'") && columnDefault.endsWith("'")) {
                columnDefault = columnDefault.substring(2, columnDefault.length() - 1);
            }
            // 兼容逻辑：如果是字符串类型，使用者在默认值前后携带了''，则在比对的时候自动去掉
            if (paramType.isCharString() && defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
                defaultValue = defaultValue.substring(1, defaultValue.length() - 1);
            }
            return !defaultValue.equals(columnDefault);
        }
        return false;
    }

    /**
     * 字段类型比对是否需要改变
     */
    private static boolean isFieldTypeChanged(InformationSchemaColumn informationSchemaColumn, MysqlColumnMetadata mysqlColumnMetadata) {

        TypeAndLength fieldType = mysqlColumnMetadata.getType();
        // 整数类型，只对比类型，不对比长度
        if (fieldType.isNumber()) {
            return !fieldType.typeName().equalsIgnoreCase(informationSchemaColumn.getDataType());
        }
        // 非整数类型，类型全文匹配：varchar(255) double(6,2)
        String fullType = fieldType.getFullType();
        return !fullType.equals(informationSchemaColumn.getColumnType().toLowerCase());
    }

    private static boolean isCommentChanged(InformationSchemaColumn informationSchemaColumn, MysqlColumnMetadata mysqlColumnMetadata) {
        String fieldComment = mysqlColumnMetadata.getComment();
        return !StringUtils.isEmpty(fieldComment) && !fieldComment.equals(informationSchemaColumn.getColumnComment());
    }

    private static void compareTableProperties(MysqlTableMetadata mysqlTableMetadata, InformationSchemaTable tableInformation, MysqlCompareTableInfo mysqlCompareTableInfo) {
        String tableComment = mysqlTableMetadata.getComment();
        String tableCharset = mysqlTableMetadata.getCharacterSet();
        String tableCollate = mysqlTableMetadata.getCollate();
        String tableEngine = mysqlTableMetadata.getEngine();
        // 判断表注释是否要更新
        if (!StringUtils.isEmpty(tableComment) && !tableComment.equals(tableInformation.getTableComment())) {
            mysqlCompareTableInfo.setComment(tableComment);
        }
        // 判断表字符集是否要更新
        if (!StringUtils.isEmpty(tableCharset)) {
            String collate = tableInformation.getTableCollation();
            if (!StringUtils.isEmpty(collate)) {
                String charset = collate.substring(0, collate.indexOf("_"));
                if (!tableCharset.equals(charset) || !tableCollate.equals(collate)) {
                    mysqlCompareTableInfo.setCharacterSet(tableCharset);
                    mysqlCompareTableInfo.setCollate(tableCollate);
                }
            }
        }
        // 判断表引擎是否要更新
        if (!StringUtils.isEmpty(tableEngine) && !tableEngine.equals(tableInformation.getEngine())) {
            mysqlCompareTableInfo.setEngine(tableEngine);
        }
    }
}
