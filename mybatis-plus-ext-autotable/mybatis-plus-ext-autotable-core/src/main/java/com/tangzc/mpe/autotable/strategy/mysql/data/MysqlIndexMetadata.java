package com.tangzc.mpe.autotable.strategy.mysql.data;

import com.tangzc.mpe.autotable.annotation.Index;
import com.tangzc.mpe.autotable.annotation.IndexField;
import com.tangzc.mpe.autotable.annotation.TableIndex;
import com.tangzc.mpe.autotable.annotation.enums.IndexSortTypeEnum;
import com.tangzc.mpe.autotable.annotation.enums.IndexTypeEnum;
import com.tangzc.mpe.autotable.strategy.mysql.data.enums.MySqlIndexFunctionEnum;
import com.tangzc.mpe.magic.TableColumnUtil;
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
public class MysqlIndexMetadata {

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
     * 索引方法
     */
    private MySqlIndexFunctionEnum function;

    /**
     * 索引注释
     */
    private String comment;

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor(staticName = "newInstance")
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

    public static MysqlIndexMetadata create(Field field, String indexPrefix) {
        // 获取当前字段的@Index注解
        Index index = AnnotatedElementUtils.findMergedAnnotation(field, Index.class);
        if (null != index) {
            String realColumnName = TableColumnUtil.getRealColumnName(field);
            MysqlIndexMetadata mysqlIndexMetadata = new MysqlIndexMetadata();
            String indexName = index.name();
            if (StringUtils.isEmpty(indexName)) {
                indexName = TableColumnUtil.getRealColumnName(field);
            }
            mysqlIndexMetadata.setName(indexPrefix + indexName);
            mysqlIndexMetadata.setType(index.type());
            if (StringUtils.hasText(index.function())) {
                mysqlIndexMetadata.setFunction(MySqlIndexFunctionEnum.parse(index.function()));
            }
            mysqlIndexMetadata.setComment(index.comment());
            mysqlIndexMetadata.getColumns().add(MysqlIndexMetadata.IndexColumnParam.newInstance(realColumnName, null));
            return mysqlIndexMetadata;
        }
        return null;
    }

    public static MysqlIndexMetadata create(Class<?> clazz, TableIndex tableIndex, String indexPrefix) {

        // 获取当前字段的@Index注解
        if (null != tableIndex) {

            List<IndexColumnParam> columnParams = getColumnParams(clazz, tableIndex);

            MysqlIndexMetadata mysqlIndexMetadata = new MysqlIndexMetadata();
            mysqlIndexMetadata.setName(indexPrefix + tableIndex.name());
            mysqlIndexMetadata.setType(tableIndex.type());
            if (StringUtils.hasText(tableIndex.function())) {
                mysqlIndexMetadata.setFunction(MySqlIndexFunctionEnum.parse(tableIndex.function()));
            }
            mysqlIndexMetadata.setComment(tableIndex.comment());
            mysqlIndexMetadata.setColumns(columnParams);
            return mysqlIndexMetadata;
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
                                String realColumnName = TableColumnUtil.getRealColumnName(clazz, sortField.field());
                                // 重复字段，自动排除忽略掉
                                if (exitsColumns.contains(realColumnName)) {
                                    return null;
                                }
                                exitsColumns.add(realColumnName);
                                return IndexColumnParam.newInstance(realColumnName, sortField.sort());
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
                                String realColumnName = TableColumnUtil.getRealColumnName(clazz, field);
                                // 重复字段，自动排除忽略掉
                                if (exitsColumns.contains(realColumnName)) {
                                    return null;
                                }
                                exitsColumns.add(realColumnName);
                                return IndexColumnParam.newInstance(realColumnName, null);
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
        }

        return columnParams;
    }
}
