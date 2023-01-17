package com.tangzc.mpe.autotable.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.metadata.AnnotatedElementUtilsPlus;
import com.baomidou.mybatisplus.core.metadata.impl.TableIdImpl;
import com.google.common.collect.Sets;
import com.tangzc.mpe.autotable.annotation.*;
import com.tangzc.mpe.autotable.annotation.impl.*;
import com.tangzc.mpe.autotable.strategy.IgnoreExt;
import com.tangzc.mpe.autotable.strategy.FieldTypeHandler;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author don
 */
public class TableBeanUtils {

    private static Map<Class<?>, HashSet<String>> excludeFieldsMap = new HashMap<>();

    private static List<IgnoreExt> ignoreExts;

    private static List<FieldTypeHandler> fieldTypeHandlers;

    public static boolean isIncludeField(Field field, Class<?> clazz) {

        if (ignoreExts == null) {
            ignoreExts = SpringContextUtil.getBeansOfTypeList(IgnoreExt.class);
        }

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

    public static List<TableIndex> getTableIndexes(Class<?> clazz) {
        List<TableIndex> tableIndices = new ArrayList<>();
        TableIndexes tableIndexes = AnnotatedElementUtils.findMergedAnnotation(clazz, TableIndexes.class);
        if (tableIndexes != null) {
            for (TableIndex tableIndex : tableIndexes.value()) {
                tableIndices.add(tableIndex);
            }
        }
        TableIndex tableIndex = AnnotatedElementUtils.findMergedAnnotation(clazz, TableIndex.class);
        if (tableIndex != null) {
            tableIndices.add(tableIndex);
        }
        return tableIndices;
    }


    public static boolean isPrimary(Field field) {

        if (AnnotatedElementUtilsPlus.hasAnnotation(field, TableId.class)) {
            return true;
        }
        Primary column = AnnotatedElementUtilsPlus.findMergedAnnotation(field, Primary.class, PrimaryImpl.class);
        if (column != null && column.value()) {
            return true;
        }

        return "id".equals(field.getName());
    }

    public static boolean isAutoIncrement(Field field) {
        TableId tableId = AnnotatedElementUtilsPlus.findMergedAnnotation(field, TableId.class, TableIdImpl.class);
        return tableId != null && tableId.type() == IdType.AUTO;
    }

    public static Boolean isNotNull(Field field) {
        // 主键默认为非空
        if (isPrimary(field)) {
            return true;
        }

        NotNull column = AnnotatedElementUtilsPlus.findMergedAnnotation(field, NotNull.class, NotNullImpl.class);
        if (column != null) {
            return column.value();
        }
        return false;
    }

    public static String getComment(Field field) {
        ColumnComment column = AnnotatedElementUtilsPlus.findMergedAnnotation(field, ColumnComment.class, ColumnCommentImpl.class);
        if (column != null) {
            return column.value();
        }
        return "";
    }

    public static ColumnDefault getDefaultValue(Field field) {
        return AnnotatedElementUtilsPlus.findMergedAnnotation(field, ColumnDefault.class, ColumnDefaultImpl.class);
    }

    public static ColumnType getColumnType(Field field) {
        return AnnotatedElementUtilsPlus.findMergedAnnotation(field, ColumnType.class, ColumnTypeImpl.class);
    }

    public static Class<?> getFieldType(Class<?> clazz, Field field) {

        // 自定义获取字段的类型
        if (fieldTypeHandlers == null) {
            fieldTypeHandlers = SpringContextUtil.getBeansOfTypeList(FieldTypeHandler.class);
        }
        Class<?> fieldType = fieldTypeHandlers.stream()
                .map(handler -> handler.getFieldType(clazz, field))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (fieldType == null) {
            fieldType = field.getType();
        }

        return fieldType;
    }
}
