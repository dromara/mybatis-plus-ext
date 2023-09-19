package com.tangzc.mpe.autotable.strategy.mysql.builder;

import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlColumnMetadata;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlCompareTableInfo;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlIndexMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Slf4j
public class ModifyTableSqlBuilder {

    /**
     * 构建创建新表的SQL
     *
     * @param mysqlCompareTableInfo 参数
     * @return sql
     */
    public static String buildSql(MysqlCompareTableInfo mysqlCompareTableInfo) {

        String name = mysqlCompareTableInfo.getName();

        String collate = mysqlCompareTableInfo.getCollate();
        String engine = mysqlCompareTableInfo.getEngine();
        String characterSet = mysqlCompareTableInfo.getCharacterSet();
        String comment = mysqlCompareTableInfo.getComment();

        List<String> dropColumnList = mysqlCompareTableInfo.getDropColumnList();
        List<MysqlColumnMetadata> modifyMysqlColumnMetadataList = mysqlCompareTableInfo.getModifyMysqlColumnMetadataList();
        List<MysqlColumnMetadata> mysqlColumnMetadataList = mysqlCompareTableInfo.getMysqlColumnMetadataList();
        List<String> dropIndexList = mysqlCompareTableInfo.getDropIndexList();
        List<MysqlIndexMetadata> mysqlIndexMetadataList = mysqlCompareTableInfo.getMysqlIndexMetadataList();

        // 记录所有修改项，（利用数组结构，便于添加,分割）
        List<String> modifyItems = new ArrayList<>();

        // 删除表字段处理
        modifyItems.add(
                dropColumnList.stream()
                        .map(dropColumn -> "DROP COLUMN `{columnName}`"
                                .replace("{columnName}", dropColumn)
                        ).collect(Collectors.joining(","))
        );

        // 修改表字段处理
        modifyItems.add(
                modifyMysqlColumnMetadataList.stream().map(modifyColumn -> {
                    // 判断是主键，自动设置为NOT NULL，并记录
                    if (modifyColumn.isPrimary()) {
                        modifyColumn.setNotNull(true);
                    }
                    // 拼接每个字段的sql片段
                    String columnSql = modifyColumn.toColumnSql();
                    return "MODIFY COLUMN " + columnSql;
                }).collect(Collectors.joining(","))
        );

        // 新增表字段处理
        modifyItems.add(
                mysqlColumnMetadataList.stream().map(addColumn -> {
                    // 判断是主键，自动设置为NOT NULL，并记录
                    if (addColumn.isPrimary()) {
                        addColumn.setNotNull(true);
                    }
                    // 拼接每个字段的sql片段
                    String columnSql = addColumn.toColumnSql();
                    return "ADD COLUMN " + columnSql;
                }).collect(Collectors.joining(","))
        );

        /*
        处理主键
         */
        // 判断是否需要删除原有主键
        if (mysqlCompareTableInfo.isDropPrimary()) {
            modifyItems.add("DROP PRIMARY KEY");
        }
        // 判断是否存在新的主键，添加
        if (!mysqlCompareTableInfo.getNewPrimaries().isEmpty()) {
            List<String> primaries = mysqlCompareTableInfo.getNewPrimaries().stream()
                    .map(MysqlColumnMetadata::getName)
                    .collect(Collectors.toList());
            String primaryKeySql = CreateTableSqlBuilder.getPrimaryKeySql(primaries);
            modifyItems.add("ADD " + primaryKeySql);
        }

        // 删除索引
        modifyItems.add(
                dropIndexList.stream()
                        .map(dropIndex -> "DROP INDEX `{indexName}`"
                                .replace("{indexName}", dropIndex))
                        .collect(Collectors.joining(","))
        );

        // 添加索引
        modifyItems.add(
                mysqlIndexMetadataList.stream().map(indexParam -> {
                    String indexSql = CreateTableSqlBuilder.getIndexSql(indexParam);
                    return "ADD " + indexSql;
                }).collect(Collectors.joining(","))
        );

        // 引擎，相较于新增表，多了","前缀
        if (!StringUtils.isEmpty(engine)) {
            modifyItems.add("ENGINE = " + engine);
        }
        // 字符集，相较于新增表，多了","前缀
        if (!StringUtils.isEmpty(characterSet)) {
            modifyItems.add("CHARACTER SET = " + characterSet);
        }
        // 排序，相较于新增表，多了","前缀
        if (!StringUtils.isEmpty(collate)) {
            modifyItems.add("COLLATE = " + collate);
        }
        // 备注，相较于新增表，多了","前缀
        if (StringUtils.hasText(comment)) {
            modifyItems.add(
                    "COMMENT = '{comment}'"
                            .replace("{comment}", comment)
            );
        }

        // 组合sql: 过滤空字符项，逗号拼接
        String modifySql = modifyItems.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(","));

        return "ALTER TABLE `{tableName}` {modifyItems};"
                .replace("{tableName}", name)
                .replace("{modifyItems}", modifySql);
    }
}
