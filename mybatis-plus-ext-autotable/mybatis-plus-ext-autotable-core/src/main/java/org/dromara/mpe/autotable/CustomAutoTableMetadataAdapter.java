package org.dromara.mpe.autotable;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.dromara.autotable.annotation.ColumnDefault;
import org.dromara.autotable.annotation.enums.DefaultValueEnum;
import org.dromara.autotable.core.AutoTableMetadataAdapter;
import org.dromara.mpe.magic.MybatisPlusProperties;
import org.dromara.mpe.magic.util.AnnotatedElementUtilsPlus;
import org.dromara.mpe.magic.util.TableColumnNameUtil;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author don
 */
public class CustomAutoTableMetadataAdapter implements AutoTableMetadataAdapter {

    private final List<IgnoreExt> ignoreExts;

    public CustomAutoTableMetadataAdapter(List<IgnoreExt> ignoreExts) {
        this.ignoreExts = ignoreExts;
    }

    @Override
    public Boolean isIgnoreField(Field field, Class<?> clazz) {

        TableField tableField = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableField.class);
        boolean ignore = tableField != null && !tableField.exist();
        if (ignore) {
            return true;
        }

        // 通过excludeProperty判断是否忽略
        TableName tableName = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableName.class);
        if (tableName != null) {
            return Arrays.stream(tableName.excludeProperty()).anyMatch(property -> property.equals(field.getName()));
        }

        // 外部框架检测钩子
        for (IgnoreExt ignoreExt : ignoreExts) {
            boolean isIgnoreField = ignoreExt.isIgnoreField(field, clazz);
            if (isIgnoreField) {
                return false;
            }
        }

        return false;
    }

    @Override
    public Boolean isPrimary(Field field, Class<?> clazz) {
        if (AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableId.class) != null) {
            return true;
        }

        return "id".equals(field.getName());
    }

    @Override
    public Boolean isAutoIncrement(Field field, Class<?> clazz) {

        if (!isPrimary(field, clazz)) {
            return false;
        }

        TableId tableId = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableId.class);
        return tableId != null && tableId.type() == IdType.AUTO;
    }

    @Override
    public List<String> getColumnEnumValues(Class<?> enumClassType) {
        if (enumClassType.isEnum()) {
            Field valField = Arrays.stream(enumClassType.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(EnumValue.class))
                    .findFirst()
                    .orElse(null);
            if (valField != null) {
                // 设置私有字段可访问
                valField.setAccessible(true);
                return Arrays.stream(enumClassType.getEnumConstants())
                        .map(enumConstant -> {
                            try {
                                return valField.get(enumConstant);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .map(Objects::toString)
                        .collect(Collectors.toList());
            } else {
                return Arrays.stream(enumClassType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.toList());
            }
        } else {
            throw new IllegalArgumentException(String.format("Class: %s 非枚举类型", enumClassType.getName()));
        }
    }

    @Override
    public String getTableName(Class<?> clazz) {

        return TableColumnNameUtil.getTableName(clazz);
    }

    @Override
    public String getTableSchema(Class<?> clazz) {

        TableName mybatisPlusTableName = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(clazz, TableName.class);
        if (mybatisPlusTableName != null && StringUtils.hasText(mybatisPlusTableName.schema())) {
            return mybatisPlusTableName.schema();
        }
        return null;
    }

    /**
     * 根据注解顺序和配置，获取字段对应的数据库字段名
     *
     * @param field
     * @return
     */
    @Override
    public String getColumnName(Class<?> clazz, Field field) {

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

    @Override
    public ColumnDefault getColumnDefaultValue(Field field, Class<?> clazz) {

        // 当没有配置任何默认值，且mybatis-plus配置了logic的默认 常规值的情况下，设置为默认值
        boolean isLogicDeleteField = Objects.equals(MybatisPlusProperties.logicDeleteField, field.getName());
        String logicNotDeleteValue = MybatisPlusProperties.logicNotDeleteValue;
        if(isLogicDeleteField && StringUtils.hasText(logicNotDeleteValue)) {
            return new ColumnDefault() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public DefaultValueEnum type() {
                    return null;
                }

                @Override
                public String value() {
                    return logicNotDeleteValue;
                }
            };
        }
        return AutoTableMetadataAdapter.super.getColumnDefaultValue(field, clazz);
    }

    private static String filterSpecialChar(String name) {

        return name.replaceAll("`", "");
    }

    public static String smartConvert(Boolean camelToUnderline, String column) {

        // 表上单独开启字段下划线申明
        if (camelToUnderline != null && camelToUnderline) {
            column = com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(column);
        }

        // 全局大写命名
        if (MybatisPlusProperties.capitalMode) {
            column = column.toUpperCase();
        }

        return column;
    }
}
