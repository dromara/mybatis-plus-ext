package com.tangzc.mpe.autotable.strategy.pgsql.builder;

import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.autotable.annotation.TableIndex;
import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlColumnMetadata;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlIndexMetadata;
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlTableMetadata;
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

    public static PgsqlTableMetadata build(Class<?> clazz) {

        String tableName = TableColumnNameUtil.getTableName(clazz);

        PgsqlTableMetadata pgsqlTableMetadata = new PgsqlTableMetadata();
        pgsqlTableMetadata.setTableName(tableName);

        Table tableAnno = AnnotatedElementUtils.findMergedAnnotation(clazz, Table.class);
        assert tableAnno != null;
        // 获取表注释
        pgsqlTableMetadata.setComment(tableAnno.comment());

        List<Field> fields = BeanClassUtil.getAllDeclaredFields(clazz);
        pgsqlTableMetadata.setColumnMetadataList(getColumnList(clazz, fields));
        pgsqlTableMetadata.setIndexMetadataList(getIndexList(clazz, fields));

        return pgsqlTableMetadata;
    }

    public static List<PgsqlColumnMetadata> getColumnList(Class<?> clazz, List<Field> fields) {
        return fields.stream()
                .filter(field -> TableBeanUtils.isIncludeField(field, clazz))
                .map(field -> PgsqlColumnMetadata.create(clazz, field))
                .collect(Collectors.toList());
    }

    public static List<PgsqlIndexMetadata> getIndexList(Class<?> clazz, List<Field> fields) {

        IndexRepeatChecker indexRepeatChecker = IndexRepeatChecker.of();

        // 类上的索引注解
        List<TableIndex> tableIndexes = TableBeanUtils.getTableIndexes(clazz);
        List<PgsqlIndexMetadata> indexMetadataList = tableIndexes.stream()
                .map(tableIndex -> PgsqlIndexMetadata.create(clazz, tableIndex, getAutoTableProperties().getIndexPrefix()))
                .filter(Objects::nonNull)
                .filter(indexMetadata -> indexRepeatChecker.filter(indexMetadata.getName()))
                .collect(Collectors.toList());

        // 字段上的索引注解
        List<PgsqlIndexMetadata> onFieldIndexMetadata = fields.stream()
                .filter(field -> TableBeanUtils.isIncludeField(field, clazz))
                .map(field -> PgsqlIndexMetadata.create(field, getAutoTableProperties().getIndexPrefix()))
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
