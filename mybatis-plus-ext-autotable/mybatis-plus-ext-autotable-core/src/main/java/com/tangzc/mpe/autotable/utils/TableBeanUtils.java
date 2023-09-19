package com.tangzc.mpe.autotable.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.magic.AnnotatedElementUtilsPlus;
import com.google.common.collect.Sets;
import com.tangzc.mpe.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.ColumnDefault;
import com.tangzc.mpe.autotable.annotation.ColumnType;
import com.tangzc.mpe.autotable.annotation.NotNull;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.autotable.annotation.TableIndex;
import com.tangzc.mpe.autotable.annotation.TableIndexes;
import com.tangzc.mpe.autotable.strategy.FieldTypeHandler;
import com.tangzc.mpe.autotable.strategy.IgnoreExt;
import com.tangzc.mpe.magic.util.SpringContextUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                excludes = Sets.newHashSet(table.excludeProperty());
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
            Collections.addAll(tableIndices, tableIndexes.value());
        }
        TableIndex tableIndex = AnnotatedElementUtils.findMergedAnnotation(clazz, TableIndex.class);
        if (tableIndex != null) {
            tableIndices.add(tableIndex);
        }
        return tableIndices;
    }


    public static boolean isPrimary(Field field) {

        if (AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableId.class) != null) {
            return true;
        }

        return "id".equals(field.getName());
    }

    public static boolean isAutoIncrement(Field field) {
        TableId tableId = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableId.class);
        return tableId != null && tableId.type() == IdType.AUTO;
    }

    public static Boolean isNotNull(Field field) {
        // 主键默认为非空
        if (isPrimary(field)) {
            return true;
        }

        NotNull column = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, NotNull.class);
        if (column != null) {
            return column.value();
        }
        return false;
    }

    public static String getComment(Field field) {
        ColumnComment column = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, ColumnComment.class);
        if (column != null) {
            return column.value();
        }
        return "";
    }

    public static ColumnDefault getDefaultValue(Field field) {
        return AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, ColumnDefault.class);
    }

    public static ColumnType getColumnType(Field field) {
        return AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, ColumnType.class);
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
