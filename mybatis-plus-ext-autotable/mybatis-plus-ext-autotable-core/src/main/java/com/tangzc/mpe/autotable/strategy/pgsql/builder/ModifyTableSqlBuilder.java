package com.tangzc.mpe.autotable.strategy.pgsql.builder;

import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlColumnMetadata;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlCompareTableInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Slf4j
public class ModifyTableSqlBuilder {

    /**
     * 构建创建新表的SQL
     *
     * @param pgsqlCompareTableInfo 参数
     * @return sql
     */
    public static String buildSql(PgsqlCompareTableInfo pgsqlCompareTableInfo) {

        String tableName = pgsqlCompareTableInfo.getName();

        String tableComment = pgsqlCompareTableInfo.getComment();
        Map<String, String> columnComment = pgsqlCompareTableInfo.getColumnComment();
        Map<String, String> indexComment = pgsqlCompareTableInfo.getIndexComment();

        /* 修改字段 */
        List<String> alterTableSqlList = new ArrayList<>();
        // 删除主键
        String primaryKeyName = pgsqlCompareTableInfo.getDropPrimaryKeyName();
        if (StringUtils.hasText(primaryKeyName)) {
            alterTableSqlList.add("  DROP CONSTRAINT \"" + primaryKeyName + "\"");
        }
        // 删除列
        List<String> dropColumnList = pgsqlCompareTableInfo.getDropColumnList();
        dropColumnList.stream()
                .map(columnName -> "  DROP COLUMN \"" + columnName + "\"")
                .forEach(alterTableSqlList::add);
        // 新增列
        List<PgsqlColumnMetadata> newColumnList = pgsqlCompareTableInfo.getNewColumnMetadataList();
        newColumnList.stream()
                .map(column -> "  ADD COLUMN " + column.toColumnSql())
                .forEach(alterTableSqlList::add);
        // 修改列
        List<PgsqlColumnMetadata> modifyColumnList = pgsqlCompareTableInfo.getModifyColumnMetadataList();
        for (PgsqlColumnMetadata columnMetadata : modifyColumnList) {
            // 修改字段
            String columnName = columnMetadata.getName();
            // 类型
            alterTableSqlList.add("  ALTER COLUMN \"" + columnName + "\" TYPE " + columnMetadata.getType().getFullType());
            // 非空
            alterTableSqlList.add("  ALTER COLUMN \"" + columnName + "\" " + (columnMetadata.isNotNull() ? "SET" : "DROP") + " NOT NULL");
            // 默认值
            String defaultVal = "NULL";
            DefaultValueEnum defaultValueType = columnMetadata.getDefaultValueType();
            if (DefaultValueEnum.EMPTY_STRING == defaultValueType) {
                defaultVal = "''";
            } else {
                String defaultValue = columnMetadata.getDefaultValue();
                if (StringUtils.hasText(defaultValue)) {
                    defaultVal = defaultValue;
                }
            }
            alterTableSqlList.add("  ALTER COLUMN \"" + columnName + "\" SET DEFAULT " + defaultVal);
        }
        // 添加主键
        List<PgsqlColumnMetadata> newPrimaries = pgsqlCompareTableInfo.getNewPrimaries();
        if (!newPrimaries.isEmpty()) {
            String primaryColumns = newPrimaries.stream().map(col -> "\"" + col.getName() + "\"").collect(Collectors.joining(", "));
            if (StringUtils.hasText(primaryKeyName)) {
                // 修改主键
                alterTableSqlList.add("  ADD CONSTRAINT \"" + primaryKeyName + "\" PRIMARY KEY (" + primaryColumns + ")");
            } else {
                // 新增主键
                alterTableSqlList.add("  ADD PRIMARY KEY (" + primaryColumns + ")");
            }
        }
        // 组合sql
        String alterTableSql = alterTableSqlList.isEmpty() ? "" : ("ALTER TABLE \"" + tableName + "\" \n" + String.join(",\n", alterTableSqlList) + ";");

        /* 为 表、字段、索引 添加注释 */
        String addColumnCommentSql = CreateTableSqlBuilder.getAddColumnCommentSql(tableName, tableComment, columnComment, indexComment);

        /* 修改 索引 */
        // 删除索引
        List<String> dropIndexList = pgsqlCompareTableInfo.getDropIndexList();
        String dropIndexSql = dropIndexList.stream().map(indexName -> "DROP INDEX \"public\".\"" + indexName + "\";").collect(Collectors.joining("\n"));
        // 添加索引
        String createIndexSql = CreateTableSqlBuilder.getCreateIndexSql(tableName, pgsqlCompareTableInfo.getIndexMetadataList());


        return dropIndexSql + "\n" + alterTableSql + "\n" + addColumnCommentSql + "\n" + createIndexSql;
    }
}
