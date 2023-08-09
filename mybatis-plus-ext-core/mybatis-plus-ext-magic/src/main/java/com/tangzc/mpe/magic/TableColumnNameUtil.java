package com.tangzc.mpe.magic;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * @author don
 */
public class TableColumnNameUtil {

    public static String filterSpecialChar(String name) {
        return name.replaceAll("`", "");
    }

    public static String getTableName(Class<?> clazz) {

        String finalTableName = "";

        TableName mybatisPlusTableName = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(clazz, TableName.class);
        if (mybatisPlusTableName != null && StringUtils.hasText(mybatisPlusTableName.value())) {
            finalTableName = mybatisPlusTableName.value();
        }
        if (!StringUtils.hasText(finalTableName)) {
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
        TableField tableField = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableField.class);
        if (tableField != null && StringUtils.hasText(tableField.value()) && tableField.exist()) {
            return filterSpecialChar(tableField.value());
        }
        TableId tableId = AnnotatedElementUtils.findMergedAnnotation(field, TableId.class);
        if (tableId != null && StringUtils.hasText(tableId.value())) {
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
        TableField tableField = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableField.class);
        if (tableField != null && StringUtils.hasText(tableField.value()) && tableField.exist()) {
            return filterSpecialChar(tableField.value());
        }
        TableId tableId = AnnotatedElementUtils.findMergedAnnotation(field, TableId.class);
        if (tableId != null && StringUtils.hasText(tableId.value())) {
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
