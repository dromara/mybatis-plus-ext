package org.dromara.mpe.magic.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.dromara.mpe.magic.MybatisPlusProperties;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author don
 */
public class TableColumnNameUtil {

    private static final Map<Class<?>, String> TABLE_NAME_CATCH = new HashMap<>();

    public static String filterSpecialChar(String name) {
        return name.replaceAll("`", "");
    }

    public static String getTableName(Class<?> clazz) {

        return TABLE_NAME_CATCH.computeIfAbsent(clazz, k -> {

            String finalTableName = "";

            boolean addTablePrefix = StringUtils.hasText(MybatisPlusProperties.tablePrefix);

            TableName mybatisPlusTableName = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(clazz, TableName.class);
            if (mybatisPlusTableName != null && StringUtils.hasText(mybatisPlusTableName.value())) {
                finalTableName = mybatisPlusTableName.value();
                if (addTablePrefix && !mybatisPlusTableName.keepGlobalPrefix()) {
                    addTablePrefix = false;
                }
            }
            if (!StringUtils.hasText(finalTableName)) {
                finalTableName = smartConvert(MybatisPlusProperties.tableUnderline, clazz.getSimpleName());
            }
            // 添加表前缀
            if (addTablePrefix) {
                finalTableName = MybatisPlusProperties.tablePrefix + finalTableName;
            }
            return finalTableName;
        });
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
        TableId tableId = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableId.class);
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
        TableId tableId = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableId.class);
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
