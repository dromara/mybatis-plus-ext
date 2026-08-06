package org.dromara.mpe.autotable;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import org.dromara.autotable.adapter.mybatisplus.MybatisPlusAdapterConfig;
import org.dromara.autotable.adapter.mybatisplus.MybatisPlusMetadataAdapter;
import org.dromara.autotable.annotation.ColumnDefault;
import org.dromara.autotable.annotation.enums.DefaultValueEnum;
import org.dromara.autotable.springboot.InitializeBeans;
import org.dromara.mpe.autotable.annotation.Column;
import org.dromara.mpe.autotable.annotation.ColumnId;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.magic.util.AnnotatedElementUtilsPlus;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ext 元数据适配器，继承 MybatisPlusMetadataAdapter 并扩展支持 ext 自定义注解。
 * <p>
 * 采用「ext 注解优先 → super 回退」模式：
 * - 先通过 {@link AnnotatedElementUtilsPlus#findDeepMergedAnnotation} 读取 ext 注解（@Table/@Column/@ColumnId）
 * - 若 ext 注解未匹配，回退到父类 {@link MybatisPlusMetadataAdapter} 读取 MP 原生注解（@TableName/@TableField/@TableId）
 *
 * @author don
 */
public class ExtMetadataAdapter extends MybatisPlusMetadataAdapter implements InitializeBeans {

    private final ObjectProvider<IgnoreExt> ignoreExtProvider;

    public ExtMetadataAdapter(MybatisPlusAdapterConfig config, ObjectProvider<IgnoreExt> ignoreExtProvider) {
        super(config);
        this.ignoreExtProvider = ignoreExtProvider;
    }

    /**
     * 获取表名
     * <p>
     * 优先读取 ext @Table.value()，显式指定的表名原样使用（与 MP 语义一致），仅经过 filterSpecialChar（去除反引号）后返回。
     * 表前缀逻辑：当全局 tablePrefix 配置存在时，仅当 @Table.keepGlobalPrefix() 为 true 时才追加前缀。
     * 若 @Table 不存在或 value 为空，回退到 super（读取 @TableName，兜底类名才做驼峰转换）。
     */
    @Override
    public String getTableName(Class<?> clazz) {
        Table table = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(clazz, Table.class);
        if (table != null && hasText(table.value())) {
            // 显式指定的表名原样使用，仅去除反引号
            String tableName = filterSpecialChar(table.value());

            // 表前缀处理：keepGlobalPrefix=true → 保持使用全局前缀；keepGlobalPrefix=false → 不追加前缀
            String tablePrefix = getConfig().getTablePrefix();
            boolean addTablePrefix = hasText(tablePrefix);
            if (addTablePrefix && !table.keepGlobalPrefix()) {
                addTablePrefix = false;
            }
            if (addTablePrefix) {
                tableName = tablePrefix + tableName;
            }
            return tableName;
        }
        return super.getTableName(clazz);
    }

    /**
     * 获取表 schema
     * <p>
     * 优先读取 ext @Table.schema()，去除反引号后返回。
     * 若 @Table 不存在或 schema 为空，回退到 super（读取 @TableName.schema()）。
     */
    @Override
    public String getTableSchema(Class<?> clazz) {
        Table table = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(clazz, Table.class);
        if (table != null && hasText(table.schema())) {
            return filterSpecialChar(table.schema());
        }
        return super.getTableSchema(clazz);
    }

    /**
     * 获取字段对应的数据库列名
     * <p>
     * 优先读取 ext @ColumnId.value()，其次读取 ext @Column.value()，
     * 显式指定的列名原样使用（与 MP 语义一致），仅经过 filterSpecialChar 后返回。
     * 若两者都不存在或值为空，回退到 super（读取 @TableField/@TableId，兜底字段名才做驼峰转换）。
     */
    @Override
    public String getColumnName(Class<?> clazz, Field field) {
        // 先尝试 @ColumnId
        ColumnId columnId = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, ColumnId.class);
        if (columnId != null && hasText(columnId.value())) {
            return filterSpecialChar(columnId.value());
        }
        // 再尝试 @Column
        Column column = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, Column.class);
        if (column != null && hasText(column.value())) {
            return filterSpecialChar(column.value());
        }
        // 回退到 super
        return super.getColumnName(clazz, field);
    }

    /**
     * 判断字段是否为主键
     * <p>
     * 优先检查 ext @ColumnId 是否存在，存在则返回 true。
     * 若不存在，回退到 super（检查 @TableId 或字段名为 "id"）。
     */
    @Override
    public Boolean isPrimary(Field field, Class<?> clazz) {
        ColumnId columnId = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, ColumnId.class);
        if (columnId != null) {
            return true;
        }
        return super.isPrimary(field, clazz);
    }

    /**
     * 判断字段是否自增
     * <p>
     * 优先读取 ext @ColumnId.mode()，若为 {@link IdType#AUTO} 则返回 true。
     * 若 @ColumnId 不存在或 mode 不为 AUTO，回退到 super。
     */
    @Override
    public Boolean isAutoIncrement(Field field, Class<?> clazz) {
        ColumnId columnId = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, ColumnId.class);
        if (columnId != null && columnId.mode() == IdType.AUTO) {
            return true;
        }
        return super.isAutoIncrement(field, clazz);
    }

    /**
     * 判断字段是否忽略（不作为数据库列）
     * <p>
     * 检查顺序：
     * 1. ext @Column.exist() 为 false → 忽略（返回 true）
     * 2. ext @Table.excludeProperty() 包含字段名 → 忽略（返回 true）
     * 3. IgnoreExt 钩子：任意实现返回 true → 不忽略（返回 false，语义反转）
     * 4. 回退到 super（检查 @Ignore/@TableField.exist/@TableName.excludeProperty）
     */
    @Override
    public Boolean isIgnoreField(Field field, Class<?> clazz) {
        // 1. @Column.exist() == false → 忽略
        Column column = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, Column.class);
        if (column != null && !column.exist()) {
            return true;
        }

        // 2. @Table.excludeProperty() 包含字段名 → 忽略
        Table table = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(clazz, Table.class);
        if (table != null) {
            String[] excludeProperty = table.excludeProperty();
            if (excludeProperty != null) {
                for (String property : excludeProperty) {
                    if (property.equals(field.getName())) {
                        return true;
                    }
                }
            }
        }

        // 3. IgnoreExt 钩子（语义反转：IgnoreExt 返回 true 表示"不应忽略"，故返回 false）
        if (ignoreExtProvider.orderedStream().anyMatch(ext -> ext.isIgnoreField(field, clazz))) {
            return false;
        }

        // 4. 回退到 super
        return super.isIgnoreField(field, clazz);
    }

    /**
     * 获取字段默认值
     * <p>
     * 检查顺序：
     * 1. 逻辑删除字段：若 config.logicDeleteField 匹配且 logicNotDeleteValue 非空 → 返回该值
     * 2. ext @Column.defaultValue()：若非空 → 返回对应的 ColumnDefault
     * 3. 回退到 super（super 也会检查逻辑删除字段，并最终回退到接口默认值 null）
     */
    @Override
    public ColumnDefault getColumnDefaultValue(Field field, Class<?> clazz) {
        // 1. 逻辑删除字段检查
        String logicDeleteField = getConfig().getLogicDeleteField();
        String logicNotDeleteValue = getConfig().getLogicNotDeleteValue();
        if (hasText(logicDeleteField) && logicDeleteField.equals(field.getName()) && hasText(logicNotDeleteValue)) {
            final String notDeleteValue = logicNotDeleteValue;
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
                    return notDeleteValue;
                }
            };
        }

        // 2. @Column.defaultValue() 检查
        Column column = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, Column.class);
        if (column != null && hasText(column.defaultValue())) {
            final DefaultValueEnum defaultValueType = column.defaultValueType();
            final String defaultValue = column.defaultValue();
            return new ColumnDefault() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return ColumnDefault.class;
                }

                @Override
                public DefaultValueEnum type() {
                    return defaultValueType;
                }

                @Override
                public String value() {
                    return defaultValue;
                }
            };
        }

        // 3. 回退到 super
        return super.getColumnDefaultValue(field, clazz);
    }

    /**
     * 获取枚举类型的所有值
     * <p>
     * 查找带有 @EnumValue 注解的字段，反射获取所有枚举常量的值。
     * 若未找到 @EnumValue 字段，使用枚举常量的 toString()。
     */
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
}
