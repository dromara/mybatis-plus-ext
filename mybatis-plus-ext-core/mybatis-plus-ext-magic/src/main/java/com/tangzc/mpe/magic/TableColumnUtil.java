package com.tangzc.mpe.magic;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.metadata.AnnotatedElementUtilsPlus;
import com.baomidou.mybatisplus.core.metadata.impl.TableFieldImpl;
import com.baomidou.mybatisplus.core.metadata.impl.TableNameImpl;
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

    public static String getTableName(Class<?> clazz) {

        String finalTableName = "";

        TableName mybatisPlusTableName = AnnotatedElementUtilsPlus.findMergedAnnotation(clazz, TableName.class, TableNameImpl.class);
        if (mybatisPlusTableName != null && !StringUtils.isEmpty(mybatisPlusTableName.value())) {
            finalTableName = mybatisPlusTableName.value();
        }
        if (StringUtils.isEmpty(finalTableName)) {
            finalTableName = smartConvert(MybatisPlusProperties.tableUnderline, clazz.getSimpleName());
        }
        // 添加表前缀
        if (StringUtils.hasText(MybatisPlusProperties.tablePrefix)) {
            finalTableName = MybatisPlusProperties.tablePrefix + finalTableName;
        }
        return finalTableName;
    }

    /**
     * 根据注解顺序和配置，获取字段对应的数据库字段名
     *
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

    /**
     * 根据注解顺序和配置，获取字段对应的数据库字段名
     *
     * @param beanClazz bean class
     * @param fieldName 字段名
     */
    public static String getRealColumnName(Class<?> beanClazz, String fieldName) {

        Field field = BeanClassUtil.getField(beanClazz, fieldName);
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

    private static String smartConvert(boolean underCamel, String column) {

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

}
