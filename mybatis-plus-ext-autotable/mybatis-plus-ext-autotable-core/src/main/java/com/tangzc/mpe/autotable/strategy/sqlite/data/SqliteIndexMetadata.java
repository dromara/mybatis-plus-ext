package com.tangzc.mpe.autotable.strategy.sqlite.data;

import com.tangzc.mpe.autotable.annotation.Index;
import com.tangzc.mpe.autotable.annotation.IndexField;
import com.tangzc.mpe.autotable.annotation.TableIndex;
import com.tangzc.mpe.autotable.annotation.enums.IndexSortTypeEnum;
import com.tangzc.mpe.autotable.annotation.enums.IndexTypeEnum;
import com.tangzc.mpe.magic.TableColumnNameUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Data
@Accessors(chain = true)
public class SqliteIndexMetadata {

    /**
     * 索引名称
     */
    private String name;

    /**
     * 索引字段
     */
    private List<IndexColumnParam> columns = new ArrayList<>();

    /**
     * 索引类型
     */
    private IndexTypeEnum type;

    /**
     * 索引注释
     */
    private String comment;

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor(staticName = "of")
    public static class IndexColumnParam {
        /**
         * 字段名称
         */
        private String column;
        /**
         * 索引排序
         */
        private IndexSortTypeEnum sort;
    }

    public static SqliteIndexMetadata create(Field field, String indexPrefix) {
        // 获取当前字段的@Index注解
        Index index = AnnotatedElementUtils.findMergedAnnotation(field, Index.class);
        if (null != index) {
            String realColumnName = TableColumnNameUtil.getRealColumnName(field);
            SqliteIndexMetadata sqliteIndexMetadata = new SqliteIndexMetadata();
            String indexName = index.name();
            if (StringUtils.isEmpty(indexName)) {
                indexName = TableColumnNameUtil.getRealColumnName(field);
            }
            sqliteIndexMetadata.setName(indexPrefix + indexName);
            sqliteIndexMetadata.setType(index.type());
            sqliteIndexMetadata.setComment(index.comment());
            sqliteIndexMetadata.getColumns().add(IndexColumnParam.of(realColumnName, null));
            return sqliteIndexMetadata;
        }
        return null;
    }

    public static SqliteIndexMetadata create(Class<?> clazz, TableIndex tableIndex, String indexPrefix) {

        // 获取当前字段的@Index注解
        if (null != tableIndex) {

            List<IndexColumnParam> columnParams = getColumnParams(clazz, tableIndex);

            SqliteIndexMetadata sqliteIndexMetadata = new SqliteIndexMetadata();
            sqliteIndexMetadata.setName(indexPrefix + tableIndex.name());
            sqliteIndexMetadata.setType(tableIndex.type());
            sqliteIndexMetadata.setComment(tableIndex.comment());
            sqliteIndexMetadata.setColumns(columnParams);
            return sqliteIndexMetadata;
        }
        return null;
    }

    private static List<IndexColumnParam> getColumnParams(Class<?> clazz, final TableIndex tableIndex) {
        List<IndexColumnParam> columnParams = new ArrayList<>();
        // 防止 两种模式设置的字段有冲突
        Set<String> exitsColumns = new HashSet<>();
        // 优先获取 带排序方式的字段
        IndexField[] sortFields = tableIndex.indexFields();
        if (sortFields.length > 0) {
            columnParams.addAll(
                    Arrays.stream(sortFields)
                            .map(sortField -> {
                                String realColumnName = TableColumnNameUtil.getRealColumnName(clazz, sortField.field());
                                // 重复字段，自动排除忽略掉
                                if (exitsColumns.contains(realColumnName)) {
                                    return null;
                                }
                                exitsColumns.add(realColumnName);
                                return IndexColumnParam.of(realColumnName, sortField.sort());
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
        }
        // 其次获取 简单模式的字段，如果重复了，跳过，以带排序方式的为准
        String[] fields = tableIndex.fields();
        if (fields.length > 0) {
            columnParams.addAll(
                    Arrays.stream(fields)
                            .map(field -> {
                                String realColumnName = TableColumnNameUtil.getRealColumnName(clazz, field);
                                // 重复字段，自动排除忽略掉
                                if (exitsColumns.contains(realColumnName)) {
                                    return null;
                                }
                                exitsColumns.add(realColumnName);
                                return IndexColumnParam.of(realColumnName, null);
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
        }

        return columnParams;
    }
}
