package com.tangzc.mpe.autotable.strategy.sqlite.builder;

import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.autotable.annotation.TableIndex;
import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteColumnMetadata;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteIndexMetadata;
import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteTableMetadata;
import com.tangzc.mpe.autotable.utils.IndexRepeatChecker;
import com.tangzc.mpe.autotable.utils.SpringContextUtil;
import com.tangzc.mpe.autotable.utils.TableBeanUtils;
import com.tangzc.mpe.magic.BeanClassUtil;
import com.tangzc.mpe.magic.TableColumnNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Slf4j
public class TableMetadataBuilder {

    private static AutoTableProperties autoTableProperties;

    public static SqliteTableMetadata build(Class<?> clazz) {

        String tableName = TableColumnNameUtil.getTableName(clazz);

        SqliteTableMetadata mysqlTableMetadata = new SqliteTableMetadata();
        mysqlTableMetadata.setTableName(tableName);

        Table tableAnno = AnnotatedElementUtils.findMergedAnnotation(clazz, Table.class);
        assert tableAnno != null;
        // 获取表注释
        mysqlTableMetadata.setComment(tableAnno.comment());

        List<Field> fields = BeanClassUtil.getAllDeclaredFields(clazz);
        mysqlTableMetadata.setColumnMetadataList(getColumnList(clazz, fields));
        mysqlTableMetadata.setIndexMetadataList(getIndexList(clazz, fields));

        return mysqlTableMetadata;
    }

    public static List<SqliteColumnMetadata> getColumnList(Class<?> clazz, List<Field> fields) {
        return fields.stream()
                .filter(field -> TableBeanUtils.isIncludeField(field, clazz))
                .map(field -> SqliteColumnMetadata.create(clazz, field))
                .collect(Collectors.toList());
    }

    public static List<SqliteIndexMetadata> getIndexList(Class<?> clazz, List<Field> fields) {

        IndexRepeatChecker indexRepeatChecker = IndexRepeatChecker.of();

        // 类上的索引注解
        List<TableIndex> tableIndexes = TableBeanUtils.getTableIndexes(clazz);
        List<SqliteIndexMetadata> indexMetadataList = tableIndexes.stream()
                .map(tableIndex -> SqliteIndexMetadata.create(clazz, tableIndex, getAutoTableProperties().getIndexPrefix()))
                .filter(Objects::nonNull)
                .filter(indexMetadata -> indexRepeatChecker.filter(indexMetadata.getName()))
                .collect(Collectors.toList());

        // 字段上的索引注解
        List<SqliteIndexMetadata> onFieldIndexMetadata = fields.stream()
                .filter(field -> TableBeanUtils.isIncludeField(field, clazz))
                .map(field -> SqliteIndexMetadata.create(field, getAutoTableProperties().getIndexPrefix()))
                .filter(Objects::nonNull)
                .filter(indexMetadata -> indexRepeatChecker.filter(indexMetadata.getName()))
                .collect(Collectors.toList());
        indexMetadataList.addAll(onFieldIndexMetadata);
        return indexMetadataList;
    }

    public static AutoTableProperties getAutoTableProperties() {

        if(autoTableProperties == null) {
            autoTableProperties = SpringContextUtil.getBeanOfType(AutoTableProperties.class);
        }

        return autoTableProperties;
    }
}