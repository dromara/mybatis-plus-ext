package com.tangzc.mpe.autotable.strategy.mysql.builder;

import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.autotable.annotation.TableIndex;
import com.tangzc.mpe.autotable.annotation.mysql.MysqlCharset;
import com.tangzc.mpe.autotable.annotation.mysql.MysqlEngine;
import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlColumnMetadata;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlIndexMetadata;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlTableMetadata;
import com.tangzc.mpe.autotable.utils.IndexRepeatChecker;
import com.tangzc.mpe.autotable.utils.TableBeanUtils;
import com.tangzc.mpe.magic.BeanClassUtil;
import com.tangzc.mpe.magic.TableColumnNameUtil;
import com.tangzc.mpe.magic.util.SpringContextUtil;
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

    public static MysqlTableMetadata build(Class<?> clazz) {

        String tableName = TableColumnNameUtil.getTableName(clazz);

        MysqlTableMetadata mysqlTableMetadata = new MysqlTableMetadata();
        mysqlTableMetadata.setTableName(tableName);

        Table tableAnno = AnnotatedElementUtils.findMergedAnnotation(clazz, Table.class);
        assert tableAnno != null;
        // 获取表注释
        mysqlTableMetadata.setComment(tableAnno.comment());

        MysqlCharset mysqlCharsetAnno = AnnotatedElementUtils.findMergedAnnotation(clazz, MysqlCharset.class);
        if (mysqlCharsetAnno != null) {
            String charset = mysqlCharsetAnno.value();
            String collate = mysqlCharsetAnno.collate();
            // 字符编码不对应，自动更正
            if (!collate.startsWith(charset)) {
                collate = charset + "_general_ci";
                // log.warn("表{}的排序规则与字符编码不匹配，自动更正为{}", tableName, collate);
            }
            // 获取表字符集
            mysqlTableMetadata.setCharacterSet(charset);
            // 字符排序
            mysqlTableMetadata.setCollate(collate);
        }

        // 获取表引擎
        MysqlEngine mysqlEngine = AnnotatedElementUtils.findMergedAnnotation(clazz, MysqlEngine.class);
        if (mysqlEngine != null) {
            mysqlTableMetadata.setEngine(mysqlEngine.value());
        }

        List<Field> fields = BeanClassUtil.getAllDeclaredFieldsExcludeStatic(clazz);
        mysqlTableMetadata.setColumnMetadataList(getColumnList(clazz, fields));
        mysqlTableMetadata.setIndexMetadataList(getIndexList(clazz, fields));

        return mysqlTableMetadata;
    }

    public static List<MysqlColumnMetadata> getColumnList(Class<?> clazz, List<Field> fields) {
        return fields.stream()
                .filter(field -> TableBeanUtils.isIncludeField(field, clazz))
                .map(field -> MysqlColumnMetadata.create(clazz, field))
                .collect(Collectors.toList());
    }

    public static List<MysqlIndexMetadata> getIndexList(Class<?> clazz, List<Field> fields) {

        IndexRepeatChecker indexRepeatChecker = IndexRepeatChecker.of();

        // 类上的索引注解
        List<TableIndex> tableIndexes = TableBeanUtils.getTableIndexes(clazz);
        List<MysqlIndexMetadata> indexMetadataList = tableIndexes.stream()
                .map(tableIndex -> MysqlIndexMetadata.create(clazz, tableIndex, getAutoTableProperties().getIndexPrefix()))
                .filter(Objects::nonNull)
                .filter(indexMetadata -> indexRepeatChecker.filter(indexMetadata.getName()))
                .collect(Collectors.toList());

        // 字段上的索引注解
        List<MysqlIndexMetadata> onFieldIndexMetadata = fields.stream()
                .filter(field -> TableBeanUtils.isIncludeField(field, clazz))
                .map(field -> MysqlIndexMetadata.create(field, getAutoTableProperties().getIndexPrefix()))
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
