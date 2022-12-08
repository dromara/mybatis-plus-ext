package com.tangzc.mpe.autotable.strategy.sqlite.data.dbdata;

import com.tangzc.mpe.autotable.annotation.enums.IndexTypeEnum;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteColumnMetadata;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteIndexMetadata;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteTableMetadata;
import com.tangzc.mpe.autotable.utils.StringHelper;
import lombok.extern.slf4j.Slf4j;
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
     * CREATE TABLE "main"."无标题" -- 测试表
     * (
     * "id" INTEGER NOT NULL AUTOINCREMENT, -- 主键
     * "name" TEXT(200) NOT NULL DEFAULT '', -- 姓名
     * "age" INTEGER(2), -- 年龄
     * "address" TEXT(500) DEFAULT 济南市, -- 地址
     * "card_id" INTEGER(11) NOT NULL, -- 身份证id
     * "card_number" text(30) NOT NULL, -- 身份证号码
     * PRIMARY KEY ("id", "card_id")
     * );
     * <p>
     * CREATE UNIQUE INDEX "main"."index_card_id"
     * ON "无标题" (
     * "card_id" ASC
     * );
     * <p>
     * CREATE INDEX "main"."index_age"
     * ON "无标题" (
     * "age" ASC,
     * "address" ASC
     * );
     *
     * @param sqliteTableMetadata 参数
     * @return sql
     */
    public static String buildSql(SqliteTableMetadata sqliteTableMetadata) {

        String name = sqliteTableMetadata.getTableName();
        String comment = sqliteTableMetadata.getComment();
        List<SqliteColumnMetadata> columnMetadataList = sqliteTableMetadata.getColumnMetadataList();

        // 获取所有主键
        List<String> primaries = new ArrayList<>();
        columnMetadataList.forEach(columnData -> {
            // 判断是主键，自动设置为NOT NULL，并记录
            if (columnData.isPrimary()) {
                columnData.setNotNull(true);
                primaries.add(columnData.getName());
            }
        });
        // 单个主键，sqlite有特殊处理，声明在列描述上，多个主键的话，像mysql一样特殊声明
        boolean isSinglePrimaryKey = primaries.size() == 1;

        // 记录所有修改项，（利用数组结构，便于添加,分割）
        List<String> addItems = new ArrayList<>();

        // 表字段处理
        addItems.add(
                columnMetadataList.stream().map(columnData -> {
                    // 拼接每个字段的sql片段
                    return columnData.toColumnSql(isSinglePrimaryKey);
                }).collect(Collectors.joining(",\n"))
        );


        // 主键
        if (!primaries.isEmpty() && !isSinglePrimaryKey) {
            String primaryKeySql = getPrimaryKeySql(primaries);
            addItems.add(primaryKeySql);
        }

        // 组合sql: 过滤空字符项，逗号拼接
        String addSql = addItems.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(","));

        String createTable = ("CREATE TABLE `{tableName}` -- {comment} \n" +
                "(\n{addItems});\n")
                .replace("{tableName}", name)
                .replace("{comment}", comment)
                .replace("{addItems}", addSql);

        // sqlite索引特殊处理
        List<SqliteIndexMetadata> indexMetadataList = sqliteTableMetadata.getIndexMetadataList();
        // 索引
        createTable += indexMetadataList.stream()
                .map(indexMetadata -> CreateTableSqlBuilder.getIndexSql(name, indexMetadata))
                // 同类型的索引，排在一起，SQL美化
                .sorted()
                .collect(Collectors.joining("\n"));

        return createTable;
    }

    /**
     * CREATE INDEX "main"."index_age"
     * ON "test_test" (
     * "age" ASC,
     * "address" ASC
     * );
     *
     * @param sqliteIndexMetadata
     * @return
     */
    public static String getIndexSql(String tableName, SqliteIndexMetadata sqliteIndexMetadata) {
        return StringHelper.newInstance("CREATE {indexType} INDEX \"{indexName}\" ON {tableName} ({columns}) {indexComment};")
                .replace("{indexType}", sqliteIndexMetadata.getType() == IndexTypeEnum.NORMAL ? "" : sqliteIndexMetadata.getType().name())
                .replace("{indexName}", sqliteIndexMetadata.getName())
                .replace("{tableName}", tableName)
                .replace("{columns}", (key) -> {
                    List<SqliteIndexMetadata.IndexColumnParam> columnParams = sqliteIndexMetadata.getColumns();
                    return columnParams.stream().map(column ->
                            // 例："name" ASC
                            "\"{column}\" {sortMode}"
                                    .replace("{column}", column.getColumn())
                                    .replace("{sortMode}", column.getSort() != null ? column.getSort().name() : "")
                    ).collect(Collectors.joining(","));
                })
                .replace("{indexComment}", StringUtils.hasText(sqliteIndexMetadata.getComment()) ? "-- " + sqliteIndexMetadata.getComment() : "")
                .toString();
    }

    public static String getPrimaryKeySql(List<String> primaries) {
        return "PRIMARY KEY ({primaries})"
                .replace(
                        "{primaries}",
                        primaries.stream()
                                .map(fieldName -> "\"" + fieldName + "\"")
                                .collect(Collectors.joining(","))
                );
    }
}
