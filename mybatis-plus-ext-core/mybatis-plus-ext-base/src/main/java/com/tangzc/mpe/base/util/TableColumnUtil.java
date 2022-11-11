package com.tangzc.mpe.base.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.metadata.AnnotatedElementUtilsPlus;
import com.baomidou.mybatisplus.core.metadata.impl.TableFieldImpl;
import com.google.common.base.CaseFormat;
import com.tangzc.mpe.magic.MybatisPlusProperties;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * @author don
 */
public class TableColumnUtil {

    public static String filterSpecialChar(String name) {
        return name.replaceAll("`", "");
    }

    public static String getTableName(Class<?> entityClass) {
        String tableName;
        TableName tableNameAnno = AnnotatedElementUtils.findMergedAnnotation(entityClass, TableName.class);
        if (tableNameAnno != null && !tableNameAnno.value().isEmpty()) {
            tableName = tableNameAnno.value();
        } else {
            tableName = smartConvert(MybatisPlusProperties.tableUnderline, entityClass.getSimpleName());
            // 添加表前缀
            if(StringUtils.hasText(MybatisPlusProperties.tablePrefix)) {
                tableName = MybatisPlusProperties.tablePrefix + tableName;
            }
        }
        return filterSpecialChar(tableName);
    }
    /**
     * 根据注解顺序和配置，获取字段对应的数据库字段名
     * @param field
     * @return
     */
    public static String getRealColumnName(Field field) {
        TableField tableField = AnnotatedElementUtilsPlus.findMergedAnnotation(field, TableField.class, TableFieldImpl.class);
        if (tableField != null && !StringUtils.isEmpty(tableField.value()) && tableField.exist()) {
            return filterSpecialChar(tableField.value());
        }
        TableId tableId = AnnotatedElementUtils.findMergedAnnotation(field, TableId.class);
        if (tableId != null && !StringUtils.isEmpty(tableId.value())) {
            return filterSpecialChar(tableId.value());
        }

        return smartConvert(MybatisPlusProperties.mapUnderscoreToCamelCase, field.getName());
    }

    public static String smartConvert(boolean underCamel, String column) {

        // 开启字段下划线申明
        if (underCamel) {
            column = com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(column);
        }
        // 全局大写命名
        if (MybatisPlusProperties.capitalMode) {
            column = column.toUpperCase();
        }

        return column;
    }

    /**
     * 驼峰转下划线
     */
    private static String humpToLine(String name) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                name).toLowerCase();
    }
}
