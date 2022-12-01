package com.tangzc.mpe.autotable.strategy.mysql;

import com.google.common.base.Functions;
import com.tangzc.mpe.autotable.annotation.enums.IndexSortTypeEnum;
import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import com.tangzc.mpe.autotable.constants.DatabaseType;
import com.tangzc.mpe.autotable.constants.RunMode;
import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import com.tangzc.mpe.autotable.strategy.IStrategy;
import com.tangzc.mpe.autotable.strategy.mysql.builder.CreateTableSqlBuilder;
import com.tangzc.mpe.autotable.strategy.mysql.builder.ModifyTableSqlBuilder;
import com.tangzc.mpe.autotable.strategy.mysql.builder.TableParamBuilder;
import com.tangzc.mpe.autotable.strategy.mysql.data.*;
import com.tangzc.mpe.autotable.strategy.mysql.data.metadata.InformationSchemaColumn;
import com.tangzc.mpe.autotable.strategy.mysql.data.metadata.InformationSchemaStatistics;
import com.tangzc.mpe.autotable.strategy.mysql.data.metadata.InformationSchemaTable;
import com.tangzc.mpe.autotable.strategy.mysql.data.metadata.MpeExecuteSqlLog;
import com.tangzc.mpe.autotable.strategy.mysql.mapper.MysqlTablesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
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
public class MysqlStrategy implements IStrategy {

    @Resource
    private AutoTableProperties autoTableProperties;

    @Resource
    private TableParamBuilder tableParamBuilder;

    @Resource
    private MysqlTablesMapper mysqlTablesMapper;

    @Override
    public DatabaseType dbType() {
        return DatabaseType.mysql;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void analyseClasses(Set<Class<?>> needCreateTable) {

        log.info("数据库类型MySQL，开始执行分析数据模型差异");
        // 检查是否开启了sql执行记录，如果开启了，再检查有没有创建记录表，没有的话自动创建
        checkExecuteSqlLogTable();

        List<TableParam> createTables = new ArrayList<>();
        List<ModifyTableParam> modifyTables = new ArrayList<>();
        // 循环全部的model
        for (Class<?> clas : needCreateTable) {
            compareTableParams(clas, createTables, modifyTables);
        }

        // 1. 创建表
        createTable(createTables);
        // 2. 修改表
        modifyTable(modifyTables);

        // 打印执行情况
        if (autoTableProperties.isPrintRunResult()) {
            printRunResult(createTables, modifyTables);
        }
    }

    private void checkExecuteSqlLogTable() {
        if (autoTableProperties.getExecuteSqlLog().isEnable()) {
            String tableName = autoTableProperties.getExecuteSqlLog().getTableName();
            if (tableName == null) {
                throw new RuntimeException("开启记录SQL执行记录后，需要配置记录表的名称");
            }
            InformationSchemaTable tableInformation = mysqlTablesMapper.findTableByTableName(tableName);
            if (tableInformation == null) {
                mysqlTablesMapper.initExecuteSqlLogTable(tableName);
            }
        }
    }

    private static void printRunResult(List<TableParam> createTables, List<ModifyTableParam> modifyTables) {

        List<String> detail = new ArrayList<>();

        int newTableCount = createTables.size();
        addDetail(detail, "-- 新增%s张表", newTableCount);

        int editTableCount = modifyTables.size();
        addDetail(detail, "-- 修改%s张表", editTableCount);

        long newColumnCount = modifyTables.stream()
                .map(ModifyTableParam::getColumnParamList)
                .mapToLong(Collection::size)
                .sum();
        addDetail(detail, "---- 新增%s个列", newColumnCount);

        long dropColumnCount = modifyTables.stream()
                .map(ModifyTableParam::getDropColumnList)
                .mapToLong(Collection::size)
                .sum();
        addDetail(detail, "---- 删除%s个列", dropColumnCount);

        long editColumnCount = modifyTables.stream()
                .map(ModifyTableParam::getModifyColumnParamList)
                .mapToLong(Collection::size)
                .sum();
        addDetail(detail, "---- 修改%s个列", editColumnCount);

        long newIndexCount = modifyTables.stream()
                .map(ModifyTableParam::getIndexParamList)
                .mapToLong(Collection::size)
                .sum();
        addDetail(detail, "---- 新增%s个索引", newIndexCount);

        long dropIndexCount = modifyTables.stream()
                .map(ModifyTableParam::getDropIndexList)
                .mapToLong(Collection::size)
                .sum();
        addDetail(detail, "---- 删除%s个索引", dropIndexCount);

        log.info("分析数据模型差异结束: " + (
                detail.size() > 0 ?
                        ("\n-----------------------------------------------\n" +
                                String.join("\n", detail) +
                                "\n-----------------------------------------------")
                        : "【无差异】"
        ));
    }

    private static void addDetail(List<String> detail, String format, long newTableCount) {
        if (newTableCount > 0) {
            detail.add(String.format(format, newTableCount));
        }
    }

    /**
     * 构建出全部表的增删改的map
     *
     * @param clazz        package中的model的Class
     * @param createTables 需要创建的表集合
     * @param modifyTables 需要修改的表集合
     */
    private void compareTableParams(Class<?> clazz, final List<TableParam> createTables, final List<ModifyTableParam> modifyTables) {

        TableParam tableParam = tableParamBuilder.build(clazz);

        List<ColumnParam> columnParamList = tableParam.getColumnParamList();
        if (columnParamList.isEmpty()) {
            log.warn("扫描发现{}没有建表字段请检查！", clazz.getName());
            return;
        }

        String tableName = tableParam.getName();
        // 如果配置文件配置的是create，表示将所有的表删掉重新创建
        if (autoTableProperties.getMode() == RunMode.create) {
            log.info("由于配置的模式是create，因此先删除表后续根据结构重建，删除表：{}", tableName);
            mysqlTablesMapper.dropTableByName(tableName);
        }

        // 先查该表是否以存在
        InformationSchemaTable tableInformation = mysqlTablesMapper.findTableByTableName(tableName);

        // 不存在时，创建新表
        if (tableInformation == null) {
            createTables.add(tableParam);
        } else {
            // 存在，进行比对
            ModifyTableParam modifyTableParam = getModifyTableParam(tableParam, tableInformation);
            if (modifyTableParam.isValid()) {
                modifyTables.add(modifyTableParam);
            }
        }
    }

    public ModifyTableParam getModifyTableParam(TableParam tableParam, InformationSchemaTable tableInformation) {

        String tableName = tableParam.getName();

        ModifyTableParam modifyTableParam = new ModifyTableParam(tableName);

        // 对比表配置有无变化
        compareTableProperties(tableParam, tableInformation, modifyTableParam);

        // 开始比对列的变化: 新增、修改、删除
        compareColumns(tableParam, tableName, modifyTableParam);

        // 开始比对 主键 和 索引 的变化
        List<InformationSchemaStatistics> informationSchemaStatistics = mysqlTablesMapper.queryTablePrimaryAndIndex(tableName);
        // 按照主键（固定值：PRIMARY）、索引名字，对所有列进行分组
        Map<String, List<InformationSchemaStatistics>> keyColumnGroupByName = informationSchemaStatistics.stream()
                .collect(Collectors.groupingBy(InformationSchemaStatistics::getIndexName));

        // 对比主键
        List<InformationSchemaStatistics> tablePrimaries = keyColumnGroupByName.remove("PRIMARY");
        comparePrimary(tableParam, modifyTableParam, tablePrimaries);

        // 对比索引, informationSchemaKeyColumnUsages中剩余的都是索引数据了
        Map<String, List<InformationSchemaStatistics>> tableIndexs = keyColumnGroupByName;
        compareIndexes(tableParam, modifyTableParam, tableIndexs);

        return modifyTableParam;
    }

    private void compareIndexes(TableParam tableParam, ModifyTableParam modifyTableParam, Map<String, List<InformationSchemaStatistics>> tableIndexs) {
        // Bean上所有的索引
        List<IndexParam> indexParamList = tableParam.getIndexParamList();
        // 以Bean上的索引开启循环，逐个匹配表上的索引
        for (IndexParam indexParam : indexParamList) {
            // 根据Bean上的索引名称获取表上的索引
            String indexName = indexParam.getName();
            // 获取表上对应索引名称的所有列
            List<InformationSchemaStatistics> theIndexColumns = tableIndexs.remove(indexName);
            if (theIndexColumns == null) {
                // 表上不存在该索引，新增
                modifyTableParam.getIndexParamList().add(indexParam);
            } else {
                // 先把表上的该索引的所有字段，按照顺序排列
                theIndexColumns = theIndexColumns.stream()
                        .sorted(Comparator.comparing(InformationSchemaStatistics::getSeqInIndex))
                        .collect(Collectors.toList());
                // 获取Bean上该索引涉及的所有字段（按照字段顺序自然排序）
                List<IndexParam.IndexColumnParam> columns = indexParam.getColumns();
                // 先初步按照索引牵扯的字段数量一不一样判断是不是需要更新索引
                if (theIndexColumns.size() != columns.size()) {
                    // 同名的索引，但是表上的字段数量跟Bean上指定的不一致，需要修改（先删除，再新增）
                    modifyTableParam.getDropIndexList().add(indexName);
                    modifyTableParam.getIndexParamList().add(indexParam);
                } else {
                    // 牵扯的字段数目一致，再按顺序逐个比较每个位置的列名及其排序方式是否相同
                    for (int i = 0; i < theIndexColumns.size(); i++) {
                        InformationSchemaStatistics informationSchemaStatistics = theIndexColumns.get(i);
                        IndexSortTypeEnum indexSort = IndexSortTypeEnum.parseFromMysql(informationSchemaStatistics.getCollation());
                        IndexParam.IndexColumnParam indexColumnParam = columns.get(i);
                        IndexSortTypeEnum indexColumnParamSort = indexColumnParam.getSort();

                        // 名字不同即不同
                        boolean nameIsDiff = !informationSchemaStatistics.getColumnName().equals(indexColumnParam.getColumn());
                        // 类注解指定排序方式不为空的情况下，与库中的值不同即不同
                        boolean sortTypeIsDiff = indexColumnParamSort != null && indexColumnParamSort != indexSort;
                        if (nameIsDiff || sortTypeIsDiff) {
                            // 同名的索引，但是表上的字段数量跟Bean上指定的不一致，需要修改（先删除，再新增）
                            modifyTableParam.getDropIndexList().add(indexName);
                            modifyTableParam.getIndexParamList().add(indexParam);
                            break;
                        }
                    }
                }
            }
        }
        // 因为上一步循环，在基于Bean上索引匹配上表中的索引后，就立即删除了表上对应的索引，所以剩下的索引都是Bean上没有声明的索引，需要根据配置判断，是否删掉多余的索引
        Set<String> needDropIndexes = tableIndexs.keySet();
        if (autoTableProperties.isAutoDropIndex() && !needDropIndexes.isEmpty()) {
            modifyTableParam.getDropIndexList().addAll(needDropIndexes);
        }
    }

    private static void comparePrimary(TableParam tableParam, ModifyTableParam modifyTableParam, List<InformationSchemaStatistics> tablePrimaries) {
        // 存在主键的话
        if (CollectionUtils.isEmpty(tablePrimaries)) {
            // 如果当前表不存在主键，则更新主键(如果Bean上有的话)
            modifyTableParam.setResetPrimary(true);
        } else {
            // 获取当前Bean上指定的主键列表，顺序按照列的自然顺序排列
            List<ColumnParam> primaries = tableParam.getColumnParamList().stream()
                    .filter(ColumnParam::isPrimary)
                    .collect(Collectors.toList());
            if (tablePrimaries.size() != primaries.size()) {
                // 主键数量不一致，需要更新
                modifyTableParam.setResetPrimary(true);
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
                        modifyTableParam.setResetPrimary(true);
                        break;
                    }
                }
            }
        }
    }

    private void compareColumns(TableParam tableParam, String tableName, ModifyTableParam modifyTableParam) {
        List<ColumnParam> columnParamList = tableParam.getColumnParamList();
        // 变形：《列名，ColumnParam》
        Map<String, ColumnParam> columnParamMap = columnParamList.stream().collect(Collectors.toMap(ColumnParam::getName, Functions.identity()));
        // 查询所有列数据
        List<InformationSchemaColumn> tableColumnList = mysqlTablesMapper.findTableEnsembleByTableName(tableName);
        for (InformationSchemaColumn informationSchemaColumn : tableColumnList) {
            String columnName = informationSchemaColumn.getColumnName();
            // 以数据库字段名，从当前Bean上取信息，获取到就从中剔除
            ColumnParam columnParam = columnParamMap.remove(columnName);
            if (columnParam != null) {
                // 取到了，则进行字段配置的比对
                boolean commentChanged = isCommentChanged(informationSchemaColumn, columnParam);
                boolean fieldTypeChanged = isFieldTypeChanged(informationSchemaColumn, columnParam);
                boolean notNullChanged = columnParam.isNotNull() != informationSchemaColumn.isNotNull();
                boolean fieldIsAutoIncrementChanged = columnParam.isAutoIncrement() != informationSchemaColumn.isAutoIncrement();
                boolean defaultValueChanged = isDefaultValueChanged(informationSchemaColumn, columnParam);
                if (commentChanged || fieldTypeChanged || notNullChanged || fieldIsAutoIncrementChanged || defaultValueChanged) {
                    // 任何一项有变化，则说明需要更新该字段
                    modifyTableParam.getModifyColumnParamList().add(columnParam);
                }
            } else {
                // 没有取到对应字段，说明库中存在的字段，Bean上不存在，根据配置，决定是否删除库上的多余字段
                if (autoTableProperties.isAutoDropColumn()) {
                    modifyTableParam.getDropColumnList().add(columnName);
                }
            }
        }
        // 因为按照表中字段已经晒过一轮Bean上的字段了，同名可以取到的均删除了，剩下的都是表中字段不存在的，需要新增
        Collection<ColumnParam> needNewColumns = columnParamMap.values();
        modifyTableParam.getColumnParamList().addAll(needNewColumns);
    }

    private static boolean isDefaultValueChanged(InformationSchemaColumn informationSchemaColumn, ColumnParam columnParam) {
        String columnDefault = informationSchemaColumn.getColumnDefault();
        DefaultValueEnum defaultValueType = columnParam.getDefaultValueType();
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
            TypeAndLength paramType = columnParam.getType();
            String defaultValue = columnParam.getDefaultValue();
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
    private static boolean isFieldTypeChanged(InformationSchemaColumn informationSchemaColumn, ColumnParam columnParam) {

        TypeAndLength fieldType = columnParam.getType();
        // 整数类型，只对比类型，不对比长度
        if (fieldType.isNumber()) {
            return !fieldType.typeName().equalsIgnoreCase(informationSchemaColumn.getDataType());
        }
        // 非整数类型，类型全文匹配：varchar(255) double(6,2)
        String fullType = fieldType.getFullType();
        return !fullType.equals(informationSchemaColumn.getColumnType().toLowerCase());
    }

    private static boolean isCommentChanged(InformationSchemaColumn informationSchemaColumn, ColumnParam columnParam) {
        String fieldComment = columnParam.getComment();
        return !StringUtils.isEmpty(fieldComment) && !fieldComment.equals(informationSchemaColumn.getColumnComment());
    }

    private static void compareTableProperties(TableParam tableParam, InformationSchemaTable tableInformation, ModifyTableParam modifyTableParam) {
        String tableComment = tableParam.getComment();
        String tableCharset = tableParam.getCharacterSet();
        String tableCollate = tableParam.getCollate();
        String tableEngine = tableParam.getEngine();
        // 判断表注释是否要更新
        if (!StringUtils.isEmpty(tableComment) && !tableComment.equals(tableInformation.getTableComment())) {
            modifyTableParam.setComment(tableComment);
        }
        // 判断表字符集是否要更新
        if (!StringUtils.isEmpty(tableCharset)) {
            String collate = tableInformation.getTableCollation();
            if (!StringUtils.isEmpty(collate)) {
                String charset = collate.substring(0, collate.indexOf("_"));
                if (!tableCharset.equals(charset) || !tableCollate.equals(collate)) {
                    modifyTableParam.setCharacterSet(tableCharset);
                    modifyTableParam.setCollate(tableCollate);
                }
            }
        }
        // 判断表引擎是否要更新
        if (!StringUtils.isEmpty(tableEngine) && !tableEngine.equals(tableInformation.getEngine())) {
            modifyTableParam.setEngine(tableEngine);
        }
    }

    /**
     * 创建表
     *
     * @param newTables 需要创建的表的信息
     */
    private void createTable(final List<TableParam> newTables) {
        // 做创建表操作
        if (newTables.size() > 0) {
            for (TableParam entry : newTables) {
                log.info("开始创建表：{}", entry.getName());
                String sqlStr = CreateTableSqlBuilder.buildSql(entry);
                log.info("执行SQL：{}", sqlStr);
                mysqlTablesMapper.executeSelect(sqlStr);
                insertExecuteSqlLog(sqlStr);
                log.info("结束创建表：{}", entry.getName());
            }
        }
    }

    /**
     * 修改表信息
     *
     * @param modifyTables 需要修改的表信息
     */
    private void modifyTable(final List<ModifyTableParam> modifyTables) {
        // 做修改表操作
        if (modifyTables.size() > 0) {
            for (ModifyTableParam entry : modifyTables) {
                String sqlStr = ModifyTableSqlBuilder.buildSql(entry);
                if (StringUtils.hasText(sqlStr)) {
                    log.info("开始修改表：{}", entry.getName());
                    log.info("执行SQL：{}", sqlStr);
                    mysqlTablesMapper.executeSelect(sqlStr);
                    insertExecuteSqlLog(sqlStr);
                    log.info("结束修改表：{}", entry.getName());
                }
            }
        }
    }

    private void insertExecuteSqlLog(String sqlStr) {
        if(autoTableProperties.getExecuteSqlLog().isEnable()) {
            MpeExecuteSqlLog mpeExecuteSqlLog = MpeExecuteSqlLog.newInstance(sqlStr);
            String tableName = autoTableProperties.getExecuteSqlLog().getTableName();
            mysqlTablesMapper.insertExecuteSqlLog(tableName, mpeExecuteSqlLog);
        }
    }
}
