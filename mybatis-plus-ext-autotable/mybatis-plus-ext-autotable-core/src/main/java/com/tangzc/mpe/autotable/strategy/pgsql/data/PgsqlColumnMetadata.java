package com.tangzc.mpe.autotable.strategy.pgsql.data;

import com.tangzc.mpe.autotable.annotation.ColumnDefault;
import com.tangzc.mpe.autotable.annotation.ColumnType;
import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import com.tangzc.mpe.autotable.strategy.pgsql.converter.JavaToPgsqlConverter;
import com.tangzc.mpe.autotable.utils.SpringContextUtil;
import com.tangzc.mpe.autotable.utils.StringHelper;
import com.tangzc.mpe.autotable.utils.TableBeanUtils;
import com.tangzc.mpe.magic.TableColumnNameUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * 用于存放创建表的字段信息
 *
 * @author don
 */
@Slf4j
@Data
public class PgsqlColumnMetadata {

    /**
     * 字段名: 不可变，变了意味着新字段
     */
    private String name;

    /**
     * 字段的备注
     */
    private String comment;

    /**
     * 字段类型
     */
    private PgsqlTypeAndLength type;

    /**
     * 字段是否非空
     */
    private boolean notNull;

    /**
     * 主键是否自增
     */
    private boolean autoIncrement;

    /**
     * <p>字段默认值类型</p>
     * <p>如果该值有值的情况下，将忽略 {@link #defaultValue} 的值</p>
     */
    private DefaultValueEnum defaultValueType;

    /**
     * <p>字段默认值</p>
     * <p>如果 {@link #defaultValueType} 有值的情况下，将忽略本字段的指定</p>
     */
    private String defaultValue;

    /**
     * 字段是否是主键
     */
    private boolean primary;

    public static PgsqlColumnMetadata create(Class<?> clazz, Field field) {
        PgsqlColumnMetadata pgsqlColumnMetadata = new PgsqlColumnMetadata();
        pgsqlColumnMetadata.setName(TableColumnNameUtil.getRealColumnName(field));
        pgsqlColumnMetadata.setType(getTypeAndLength(field, clazz));
        pgsqlColumnMetadata.setNotNull(TableBeanUtils.isNotNull(field));
        pgsqlColumnMetadata.setPrimary(TableBeanUtils.isPrimary(field));
        pgsqlColumnMetadata.setAutoIncrement(TableBeanUtils.isAutoIncrement(field));
        ColumnDefault columnDefault = TableBeanUtils.getDefaultValue(field);
        if (columnDefault != null) {
            pgsqlColumnMetadata.setDefaultValueType(columnDefault.type());
            String defaultValue = columnDefault.value();
            PgsqlTypeAndLength type = pgsqlColumnMetadata.getType();
            // 补偿逻辑：字符串类型，前后自动添加'
            if (type.isCharString() && !defaultValue.startsWith("'") && !defaultValue.endsWith("'")) {
                defaultValue = "'" + defaultValue + "'";
            }
            pgsqlColumnMetadata.setDefaultValue(defaultValue);
        }
        pgsqlColumnMetadata.setComment(TableBeanUtils.getComment(field));

        return pgsqlColumnMetadata;
    }

    /**
     * 生成字段相关的SQL片段
     */
    public String toColumnSql() {
        // 例子："name" varchar(100) NULL DEFAULT '张三' COMMENT '名称'
        // 例子："id" int4(32) NOT NULL AUTO_INCREMENT COMMENT '主键'
        return StringHelper.newInstance("\"{columnName}\" {typeAndLength} {null} {default}")
                .replace("{columnName}", this.getName())
                .replace("{typeAndLength}", (key) -> {
                    PgsqlTypeAndLength typeAndLength = this.getType();
                    /* 如果是自增，忽略指定的类型，交给pgsql自动处理，pgsql会设定int4(32)类型，
                    并自动生成一个序列：表名_字段名_seq，同时设置字段的默认值为：nextval('表名_字段名_seq'::regclass) */
                    if (this.autoIncrement) {
                        return "serial";
                    }
                    return typeAndLength.getFullType();
                })
                .replace("{null}", this.isNotNull() ? "NOT NULL" : "")
                .replace("{default}", (key) -> {
                    // 指定NULL
                    DefaultValueEnum defaultValueType = this.getDefaultValueType();
                    if (defaultValueType == DefaultValueEnum.NULL) {
                        return "DEFAULT NULL";
                    }
                    // 指定空字符串
                    if (defaultValueType == DefaultValueEnum.EMPTY_STRING) {
                        return "DEFAULT ''";
                    }
                    // 自定义
                    String defaultValue = this.getDefaultValue();
                    if (DefaultValueEnum.isInvalid(defaultValueType) && !StringUtils.isEmpty(defaultValue)) {
                        return "DEFAULT " + defaultValue;
                    }
                    return "";
                })
                .toString();
    }

    private static PgsqlTypeAndLength getTypeAndLength(Field field, Class<?> clazz) {

        ColumnType column = TableBeanUtils.getColumnType(field);
        if (column != null && StringUtils.hasText(column.value())) {
            return new PgsqlTypeAndLength(column.length(), column.decimalLength(), column.value());
        }
        // 类型为空根据字段类型去默认匹配类型
        Class<?> fieldType = TableBeanUtils.getFieldType(clazz, field);

        // 获取外部注入的自定义
        JavaToPgsqlConverter javaToPgsqlConverter = SpringContextUtil.getBeanOfType(JavaToPgsqlConverter.class);
        return javaToPgsqlConverter.convert(fieldType);
    }
}
