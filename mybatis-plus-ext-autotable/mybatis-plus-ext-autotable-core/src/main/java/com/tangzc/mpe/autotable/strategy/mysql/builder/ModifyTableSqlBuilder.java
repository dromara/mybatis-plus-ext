package com.tangzc.mpe.autotable.strategy.mysql.builder;

import com.tangzc.mpe.autotable.strategy.mysql.data.ColumnParam;
import com.tangzc.mpe.autotable.strategy.mysql.data.IndexParam;
import com.tangzc.mpe.autotable.strategy.mysql.data.ModifyTableParam;
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
     * @param modifyTableParam 参数
     * @return sql
     */
    public static String buildSql(ModifyTableParam modifyTableParam) {

        List<String> primaries = new ArrayList<>();

        String name = modifyTableParam.getName();

        String collate = modifyTableParam.getCollate();
        String engine = modifyTableParam.getEngine();
        String characterSet = modifyTableParam.getCharacterSet();
        String comment = modifyTableParam.getComment();

        List<String> dropColumnList = modifyTableParam.getDropColumnList();
        List<ColumnParam> modifyColumnParamList = modifyTableParam.getModifyColumnParamList();
        List<ColumnParam> columnParamList = modifyTableParam.getColumnParamList();
        List<String> dropIndexList = modifyTableParam.getDropIndexList();
        List<IndexParam> indexParamList = modifyTableParam.getIndexParamList();

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
                modifyColumnParamList.stream().map(modifyColumn -> {
                    // 判断是主键，自动设置为NOT NULL，并记录
                    if (modifyColumn.isPrimary()) {
                        modifyColumn.setNotNull(true);
                        primaries.add(modifyColumn.getName());
                    }
                    // 拼接每个字段的sql片段
                    String columnSql = modifyColumn.toColumnSql();
                    return "MODIFY COLUMN " + columnSql;
                }).collect(Collectors.joining(","))
        );

        // 新增表字段处理
        modifyItems.add(
                columnParamList.stream().map(addColumn -> {
                    // 判断是主键，自动设置为NOT NULL，并记录
                    if (addColumn.isPrimary()) {
                        addColumn.setNotNull(true);
                        primaries.add(addColumn.getName());
                    }
                    // 拼接每个字段的sql片段
                    String columnSql = addColumn.toColumnSql();
                    return "ADD COLUMN " + columnSql;
                }).collect(Collectors.joining(","))
        );

        // 主键, 同时判断主键是否有改变，有的话，删除原来的主键，重新创建
        // 改变的情况：现有主键集合与最新主键集合不能完全匹配，忽略顺序。
        if (modifyTableParam.isResetPrimary() && !primaries.isEmpty()) {
            String primaryKeySql = CreateTableSqlBuilder.getPrimaryKeySql(primaries);
            modifyItems.add(
                    "DROP PRIMARY KEY, ADD PRIMARY " + primaryKeySql
            );
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
                indexParamList.stream().map(indexParam -> {
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

        if (!modifySql.isEmpty()) {
            return "ALTER TABLE `{tableName}` {modifyItems};"
                    .replace("{tableName}", name)
                    .replace("{modifyItems}", modifySql);
        } else {
            return null;
        }
    }
}
