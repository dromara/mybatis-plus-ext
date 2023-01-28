package com.tangzc.mpe.autotable.strategy.pgsql.builder;

import com.tangzc.mpe.autotable.annotation.enums.IndexTypeEnum;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlColumnMetadata;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlIndexMetadata;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlTableMetadata;
import com.tangzc.mpe.autotable.utils.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Slf4j
public class CreateTableSqlBuilder {

    /**
     * 构建创建新表的SQL
     *
     * @param pgsqlTableMetadata 参数
     * @return sql
     */
    public static String buildSql(PgsqlTableMetadata pgsqlTableMetadata) {

        // 建表语句
        String createTableSql = getCreateTableSql(pgsqlTableMetadata);

        // 创建索引语句
        String createIndexSql = getCreateIndexSql(pgsqlTableMetadata);

        // 为 表、字段、索引 添加注释
        String addCommentSql = getAddColumnCommentSql(pgsqlTableMetadata);

        // 组合最终建表语句
        return createTableSql + "\n" + createIndexSql + "\n" + addCommentSql;
    }

    /**
     * CREATE UNIQUE INDEX "uni_name" ON "表名" (
     * "name"
     * );
     */
    @NotNull
    private static String getCreateIndexSql(PgsqlTableMetadata pgsqlTableMetadata) {

        String tableName = pgsqlTableMetadata.getTableName();
        return pgsqlTableMetadata.getIndexMetadataList().stream()
                .map(pgsqlIndexMetadata -> StringHelper.newInstance("CREATE {indexType} INDEX \"{indexName}\" ON \"{tableName}\" {indexFunction} ({columns})")
                        .replace("{indexType}", pgsqlIndexMetadata.getType() == IndexTypeEnum.UNIQUE ? "UNIQUE" : "")
                        .replace("{indexName}", pgsqlIndexMetadata.getName())
                        .replace("{tableName}", tableName)
                        .replace("{indexFunction}", (key) -> {
                            if (pgsqlIndexMetadata.getFunction() != null) {
                                return "USING " + pgsqlIndexMetadata.getFunction();
                            } else {
                                return "";
                            }
                        })
                        .replace("{columns}", (key) -> {
                            List<PgsqlIndexMetadata.IndexColumnParam> columnParams = pgsqlIndexMetadata.getColumns();
                            return columnParams.stream().map(column ->
                                    // 例："name" ASC
                                    "\"{column}\" {sortMode}"
                                            .replace("{column}", column.getColumn())
                                            .replace("{sortMode}", column.getSort() != null ? column.getSort().name() : "")
                            ).collect(Collectors.joining(","));
                        })
                        .toString()
                ).collect(Collectors.joining("\n"));
    }

    @NotNull
    private static String getAddColumnCommentSql(PgsqlTableMetadata pgsqlTableMetadata) {

        String tableName = pgsqlTableMetadata.getTableName();

        List<String> commentList = new ArrayList<>();

        // 表备注
        String comment = pgsqlTableMetadata.getComment();
        if (StringUtils.hasText(comment)) {
            String addTableComment = "COMMENT ON TABLE \"{tableName}\" IS '{comment}';"
                    .replace("{tableName}", tableName)
                    .replace("{comment}", comment);
            commentList.add(addTableComment);
        }

        // 字段备注
        List<PgsqlColumnMetadata> columnMetadataList = pgsqlTableMetadata.getColumnMetadataList();
        columnMetadataList.stream()
                .map(column -> "COMMENT ON COLUMN \"{tableName}\".\"{name}\" IS '{comment}'"
                        .replace("{tableName}", tableName)
                        .replace("{name}", column.getName())
                        .replace("{comment}", column.getComment()))
                .forEach(commentList::add);

        // 索引备注
        List<PgsqlIndexMetadata> indexMetadataList = pgsqlTableMetadata.getIndexMetadataList();
        indexMetadataList.stream()
                .map(index -> "COMMENT ON INDEX \"{tableName}\".\"{name}\" IS '{comment}'"
                        .replace("{tableName}", tableName)
                        .replace("{name}", index.getName())
                        .replace("{comment}", index.getComment()))
                .forEach(commentList::add);

        return String.join("\n", commentList);
    }

    @NotNull
    private static String getCreateTableSql(PgsqlTableMetadata pgsqlTableMetadata) {

        String name = pgsqlTableMetadata.getTableName();
        List<PgsqlColumnMetadata> columnMetadataList = pgsqlTableMetadata.getColumnMetadataList();

        // 记录所有修改项，（利用数组结构，便于添加,分割）
        List<String> columnList = new ArrayList<>();

        // 获取所有主键（至于表字段处理之前，为了主键修改notnull）
        List<String> primaries = new ArrayList<>();
        columnMetadataList.forEach(columnData -> {
            // 判断是主键，自动设置为NOT NULL，并记录
            if (columnData.isPrimary()) {
                columnData.setNotNull(true);
                primaries.add(columnData.getName());
            }
        });

        // 表字段处理
        columnList.add(
                columnMetadataList.stream()
                        // 拼接每个字段的sql片段
                        .map(PgsqlColumnMetadata::toColumnSql)
                        .collect(Collectors.joining(","))
        );

        // 主键
        if (!primaries.isEmpty()) {
            String primaryKeySql = getPrimaryKeySql(primaries);
            columnList.add(primaryKeySql);
        }

        // 组合sql: 过滤空字符项，逗号拼接
        String addSql = columnList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(","));

        return "CREATE TABLE `{tableName}` ({columnList});"
                .replace("{tableName}", name)
                .replace("{columnList}", addSql);
    }

    private static String getPrimaryKeySql(List<String> primaries) {
        return "PRIMARY KEY ({primaries})"
                .replace(
                        "{primaries}",
                        primaries.stream()
                                .map(fieldName -> "\"" + fieldName + "\"")
                                .collect(Collectors.joining(","))
                );
    }
}
