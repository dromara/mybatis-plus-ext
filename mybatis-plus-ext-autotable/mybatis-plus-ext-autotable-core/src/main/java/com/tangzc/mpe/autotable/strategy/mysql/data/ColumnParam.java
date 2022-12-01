package com.tangzc.mpe.autotable.strategy.mysql.data;

import com.tangzc.mpe.autotable.annotation.ColumnDefault;
import com.tangzc.mpe.autotable.annotation.ColumnType;
import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import com.tangzc.mpe.autotable.strategy.mysql.ParamValidChecker;
import com.tangzc.mpe.autotable.strategy.mysql.data.metadata.JavaToMysqlType;
import com.tangzc.mpe.autotable.utils.ColumnUtils;
import com.tangzc.mpe.autotable.utils.StringHelper;
import com.tangzc.mpe.magic.TableColumnUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * 用于存放创建表的字段信息
 *
 * @author sunchenbin, Spet
 * @version 2019/07/06
 */
@Slf4j
@Data
public class ColumnParam {

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
    private TypeAndLength type;

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

    public static ColumnParam create(Class<?> clazz, Field field) {
        ColumnParam columnParam = new ColumnParam();
        columnParam.setName(TableColumnUtil.getRealColumnName(field));
        columnParam.setType(getTypeAndLength(field, clazz));
        columnParam.setNotNull(ColumnUtils.isNotNull(field));
        columnParam.setPrimary(ColumnUtils.isPrimary(field));
        columnParam.setAutoIncrement(ColumnUtils.isAutoIncrement(field));
        ColumnDefault columnDefault = ColumnUtils.getDefaultValue(field);
        if (columnDefault != null) {
            columnParam.setDefaultValueType(columnDefault.type());
            String defaultValue = columnDefault.value();
            TypeAndLength type = columnParam.getType();
            // 补偿逻辑：类型为Boolean的时候(实际数据库为bit数字类型)，兼容 true、false
            if (type.isBoolean() && !"1".equals(defaultValue) && !"0".equals(defaultValue)) {
                if (Boolean.parseBoolean(defaultValue)) {
                    defaultValue = "1";
                } else {
                    defaultValue = "0";
                }
            }
            // 补偿逻辑：字符串类型，前后自动添加'
            if (type.isCharString() && !defaultValue.startsWith("'") && !defaultValue.endsWith("'")) {
                defaultValue = "'" + defaultValue + "'";
            }
            columnParam.setDefaultValue(defaultValue);
        }
        columnParam.setComment(ColumnUtils.getComment(field));

        /* 基础的校验逻辑 */
        ParamValidChecker.checkColumnParam(clazz, field, columnParam);

        return columnParam;
    }

    /**
     * 生成字段相关的SQL片段
     */
    public String toColumnSql() {
        // 例子：`name` varchar(100) NULL DEFAULT '张三' COMMENT '名称'
        // 例子：`id` int(32) NOT NULL AUTO_INCREMENT COMMENT '主键'
        return StringHelper.newInstance("`{columnName}` {typeAndLength} {null} {default} {autoIncrement} {columnComment}")
                .replace("{columnName}", this.getName())
                .replace("{typeAndLength}", this.getType().getFullType())
                .replace("{null}", this.isNotNull() ? "NOT NULL" : "NULL")
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
                .replace("{autoIncrement}", this.isAutoIncrement() ? "AUTO_INCREMENT" : "")
                .replace("{columnComment}", StringUtils.hasText(this.getComment()) ? "COMMENT '" + this.getComment() + "'" : "")
                .toString();
    }

    private static TypeAndLength getTypeAndLength(Field field, Class<?> clazz) {

        ColumnType column = ColumnUtils.getColumnType(field);
        if (column != null && StringUtils.hasLength(column.value())) {
            MySqlColumnTypeEnum columnTypeEnum = MySqlColumnTypeEnum.parseByLowerCaseName(column.value());
            return new TypeAndLength(column.length(), column.decimalLength(), columnTypeEnum);
        }
        // 类型为空根据字段类型去默认匹配类型
        MySqlColumnTypeEnum mysqlType = JavaToMysqlType.getSqlType(field, clazz);
        if (mysqlType == null) {
            throw new RuntimeException("字段名：" + clazz.getName() + ":" + field.getName() + "不支持" + field.getGenericType() + "类型转换到mysql类型，仅支持JavaToMysqlType类中的类型默认转换，异常抛出！");
        }
        // 默认类型可以使用column来设置长度
        if (column != null) {
            return buildTypeAndLength(mysqlType, column.length(), column.decimalLength());
        }
        return buildTypeAndLength(mysqlType, mysqlType.getLengthDefault(), mysqlType.getDecimalLengthDefault());
    }

    private static TypeAndLength buildTypeAndLength(@NonNull MySqlColumnTypeEnum columnType, Integer length, Integer decimalLength) {

        TypeAndLength targetTypeAndLength = new TypeAndLength(columnType.getLengthDefault(), columnType.getDecimalLengthDefault(), columnType);
        if (length != null && length > 0) {
            targetTypeAndLength.setLength(length);
        }
        if (decimalLength != null && decimalLength > 0) {
            targetTypeAndLength.setDecimalLength(decimalLength);
        }
        return targetTypeAndLength;
    }
}
