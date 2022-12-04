package com.tangzc.mpe.autotable.strategy.mysql.builder;

import com.google.common.collect.Sets;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.autotable.annotation.TableIndex;
import com.tangzc.mpe.autotable.annotation.TableIndexes;
import com.tangzc.mpe.autotable.annotation.mysql.MysqlCharset;
import com.tangzc.mpe.autotable.annotation.mysql.MysqlEngine;
import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import com.tangzc.mpe.autotable.strategy.IgnoreExt;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlColumnMetadata;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlIndexMetadata;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlTableMetadata;
import com.tangzc.mpe.magic.TableColumnUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Slf4j
public class TableMetadataBuilder {

    private static Map<Class<?>, HashSet<String>> excludeFieldsMap = new HashMap<>();

    @Autowired(required = false)
    private List<IgnoreExt> ignoreExts;

    @Resource
    private AutoTableProperties autoTableProperties;

    public MysqlTableMetadata build(Class<?> clazz) {

        String tableName = TableColumnUtil.getTableName(clazz);

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

        mysqlTableMetadata.setMysqlColumnMetadataList(getColumnList(clazz));
        mysqlTableMetadata.setMysqlIndexMetadataList(getIndexList(clazz));

        return mysqlTableMetadata;
    }

    public List<MysqlColumnMetadata> getColumnList(Class<?> clazz) {
        Field[] fields = getAllFields(clazz);
        return Arrays.stream(fields)
                .filter(field -> isIncludeField(field, clazz))
                .map(field -> MysqlColumnMetadata.create(clazz, field))
                .collect(Collectors.toList());
    }

    public List<MysqlIndexMetadata> getIndexList(Class<?> clazz) {

        // 标记所有的索引，用于检测重复的
        Map<String, MysqlIndexMetadata> exitsIndexes = new HashMap<>(16);
        // 索引重复检测过滤器
        Function<MysqlIndexMetadata, Boolean> filter = (indexParam) -> {

            if (indexParam == null) {
                return false;
            }

            boolean exits = exitsIndexes.containsKey(indexParam.getName());
            if (exits) {
                throw new RuntimeException("发现重复索引:" + indexParam.getName());
            } else {
                exitsIndexes.put(indexParam.getName(), indexParam);
            }

            return true;
        };

        List<MysqlIndexMetadata> indexMetadataList = new ArrayList<>();
        // 类上的索引注解集合
        TableIndexes tableIndexes = AnnotatedElementUtils.findMergedAnnotation(clazz, TableIndexes.class);
        if (tableIndexes != null) {
            List<MysqlIndexMetadata> onTableIndexMetadata = Arrays.stream(tableIndexes.value())
                    .map(tableIndex -> MysqlIndexMetadata.create(clazz, tableIndex, autoTableProperties.getIndexPrefix()))
                    .filter(filter::apply)
                    .collect(Collectors.toList());
            indexMetadataList.addAll(onTableIndexMetadata);
        }
        // 类上的索引注解
        TableIndex tableIndex = AnnotatedElementUtils.findMergedAnnotation(clazz, TableIndex.class);
        if (tableIndex != null) {
            MysqlIndexMetadata indexMetadata = MysqlIndexMetadata.create(clazz, tableIndex, autoTableProperties.getIndexPrefix());
            if (filter.apply(indexMetadata)) {
                indexMetadataList.add(indexMetadata);
            }
        }
        // 字段上的索引注解
        Field[] fields = getAllFields(clazz);
        List<MysqlIndexMetadata> onFieldIndexMetadata = Arrays.stream(fields)
                .filter(field -> isIncludeField(field, clazz))
                .map(field -> MysqlIndexMetadata.create(field, autoTableProperties.getIndexPrefix()))
                .filter(filter::apply)
                .collect(Collectors.toList());
        indexMetadataList.addAll(onFieldIndexMetadata);
        return indexMetadataList;
    }

    private Field[] getAllFields(Class<?> clas) {
        Field[] fields = clas.getDeclaredFields();
        // 判断是否有父类，如果有拉取父类的field，这里只支持多层继承
        fields = recursionParents(clas, fields);
        return fields;
    }

    private boolean isIncludeField(Field field, Class<?> clazz) {

        // 外部框架检测钩子
        for (IgnoreExt ignoreExt : ignoreExts) {
            boolean isIgnoreField = ignoreExt.isIgnoreField(field, clazz);
            if (isIgnoreField) {
                return false;
            }
        }

        // 不参与建表的字段: 增加缓存策略，提升性能
        HashSet<String> excludeFields = excludeFieldsMap.computeIfAbsent(clazz, (k) -> {
            HashSet<String> excludes = new HashSet<>();
            Table table = AnnotatedElementUtils.findMergedAnnotation(clazz, Table.class);
            if (table != null) {
                excludes = Sets.newHashSet(table.excludeFields());
            }
            return excludes;
        });
        // 当前属性名在排除建表的字段内
        return !excludeFields.contains(field.getName());
    }

    /**
     * 递归扫描父类的fields
     *
     * @param clas   类
     * @param fields 属性
     */
    @SuppressWarnings("rawtypes")
    private Field[] recursionParents(Class<?> clas, Field[] fields) {
        if (clas.getSuperclass() != null) {
            Class clsSup = clas.getSuperclass();
            List<Field> fieldList = new ArrayList<>(Arrays.asList(fields));
            // 获取当前class的所有fields的name列表
            List<String> fdNames = getFieldNames(fieldList);
            for (Field pfd : clsSup.getDeclaredFields()) {
                // 避免重载属性
                if (fdNames.contains(pfd.getName())) {
                    continue;
                }
                fieldList.add(pfd);
            }
            fields = new Field[fieldList.size()];
            int i = 0;
            for (Object field : fieldList.toArray()) {
                fields[i] = (Field) field;
                i++;
            }
            fields = recursionParents(clsSup, fields);
        }
        return fields;
    }

    private List<String> getFieldNames(List<Field> fieldList) {
        List<String> fdNames = new ArrayList<>();
        for (Field fd : fieldList) {
            fdNames.add(fd.getName());
        }
        return fdNames;
    }
}
