package com.tangzc.mpe.actable.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.metadata.AnnotatedElementUtilsPlus;
import com.baomidou.mybatisplus.core.metadata.impl.TableFieldImpl;
import com.baomidou.mybatisplus.core.metadata.impl.TableIdImpl;
import com.baomidou.mybatisplus.core.metadata.impl.TableNameImpl;
import com.google.common.base.CaseFormat;
import com.tangzc.mpe.magic.MybatisPlusProperties;
import com.tangzc.mpe.actable.annotation.*;
import com.tangzc.mpe.actable.annotation.constants.MySqlCharsetConstant;
import com.tangzc.mpe.actable.annotation.constants.MySqlEngineConstant;
import com.tangzc.mpe.actable.annotation.constants.MySqlTypeConstant;
import com.tangzc.mpe.actable.command.JavaToMysqlType;
import com.tangzc.mpe.actable.command.MySqlTypeAndLength;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ColumnUtils {

    public static final String SQL_ESCAPE_CHARACTER = "`";

    /**
     * 获取Mysql的类型，以及类型需要设置几个长度，这里构建成map的样式
     * 构建Map(字段名(小写),需要设置几个长度(0表示不需要设置，1表示需要设置一个，2表示需要设置两个))
     */
    public static Map<String, MySqlTypeAndLength> mySqlTypeAndLengthMap = new HashMap<>();

    static {
        for (MySqlTypeConstant type : MySqlTypeConstant.values()) {
            mySqlTypeAndLengthMap.put(type.toString().toLowerCase(), new MySqlTypeAndLength(type.getLengthCount(), type.getLengthDefault(), type.getDecimalLengthDefault(), type.toString().toLowerCase()));
        }
    }

    public static String getTableName(Class<?> clasz) {
        TableName tableNamePlus = AnnotatedElementUtilsPlus.findMergedAnnotation(clasz, TableName.class, TableNameImpl.class);
        EnableTimeSuffix enableTimeSuffix = AnnotatedElementUtils.findMergedAnnotation(clasz, EnableTimeSuffix.class);
        String finalTableName = "";
        if (tableNamePlus != null && !StringUtils.isEmpty(tableNamePlus.value())) {
            finalTableName = tableNamePlus.value();
        }
        if (StringUtils.isEmpty(finalTableName)) {
            if (MybatisPlusProperties.tableUnderline) {
                // 都为空时采用类名按照驼峰格式转会为表名
                finalTableName = getBuildLowerName(clasz.getSimpleName());
            } else {
                // 禁止表名自动驼峰转下划线的情况下
                finalTableName = clasz.getSimpleName();
            }
            // 添加表前缀
            if (StringUtils.hasText(MybatisPlusProperties.tablePrefix)) {
                finalTableName = MybatisPlusProperties.tablePrefix + finalTableName;
            }
        }
        if (null != enableTimeSuffix && enableTimeSuffix.value()) {
            finalTableName = appendTimeSuffix(finalTableName, enableTimeSuffix.pattern());
        }
        return finalTableName;
    }

    public static String getTableComment(Class<?> clasz) {
        TableComment tableComment = AnnotatedElementUtils.findMergedAnnotation(clasz, TableComment.class);
        if (tableComment != null) {
            return tableComment.value();
        }
        return "";
    }

    public static MySqlCharsetConstant getTableCharset(Class<?> clasz) {
        TableCharset table = AnnotatedElementUtils.findMergedAnnotation(clasz, TableCharset.class);
        if (table != null && table.value() != MySqlCharsetConstant.DEFAULT) {
            return table.value();
        }
        return null;
    }

    public static MySqlEngineConstant getTableEngine(Class<?> clasz) {
        TableEngine table = AnnotatedElementUtils.findMergedAnnotation(clasz, TableEngine.class);
        if (table != null && table.value() != MySqlEngineConstant.DEFAULT) {
            return table.value();
        }
        return null;
    }

    public static String getColumnName(Field field) {
        TableField tableField = AnnotatedElementUtilsPlus.findMergedAnnotation(field, TableField.class, TableFieldImpl.class);
        if (tableField != null && !StringUtils.isEmpty(tableField.value()) && tableField.exist()) {
            return tableField.value().replace(SQL_ESCAPE_CHARACTER, "");
        }
        TableId tableId = AnnotatedElementUtils.findMergedAnnotation(field, TableId.class);
        if (tableId != null && !StringUtils.isEmpty(tableId.value())) {
            return tableId.value().replace(SQL_ESCAPE_CHARACTER, "");
        }

        // 如果不需要字段自动驼峰转下划线
        if (!MybatisPlusProperties.mapUnderscoreToCamelCase) {
            return field.getName();
        }

        return getBuildLowerName(field.getName());
    }

    private static String getBuildLowerName(String name) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                name).toLowerCase();
    }

    public static boolean isKey(Field field, Class<?> clasz) {

        if (AnnotatedElementUtils.hasAnnotation(field, TableId.class)) {
            return true;
        }
        IsKey column = AnnotatedElementUtils.findMergedAnnotation(field, IsKey.class);
        if (column != null && column.value()) {
            return true;
        }

        return "id".equals(field.getName());
    }

    public static boolean isAutoIncrement(Field field, Class<?> clasz) {
        if (!isIncloudField(field, clasz)) {
            return false;
        }
        TableId tableId = AnnotatedElementUtilsPlus.findMergedAnnotation(field, TableId.class, TableIdImpl.class);
        return tableId != null && tableId.type() == IdType.AUTO;
    }

    public static Boolean isNull(Field field, Class<?> clasz) {
        // 主键默认为非空
        if (isKey(field, clasz)) {
            return false;
        }

        IsNotNull column = AnnotatedElementUtils.findMergedAnnotation(field, IsNotNull.class);
        if (column != null) {
            return !column.value();
        }
        return true;
    }

    public static String getComment(Field field, Class<?> clasz) {
        ColumnComment column = AnnotatedElementUtils.findMergedAnnotation(field, ColumnComment.class);
        if (column != null) {
            return column.value();
        }
        return "";
    }

    public static String getDefaultValue(Field field, Class<?> clasz) {
        ColumnDefault column = AnnotatedElementUtils.findMergedAnnotation(field, ColumnDefault.class);
        if (column != null && !column.value().isEmpty()) {
            return column.value();
        }
        return null;
    }

    public static boolean getDefaultValueNative(Field field, Class<?> clasz) {
        IsNativeDefValue isNativeDefValue = AnnotatedElementUtils.findMergedAnnotation(field, IsNativeDefValue.class);
        if (isNativeDefValue != null) {
            return isNativeDefValue.value();
        }
        if (field.getGenericType().toString().equals("class java.lang.String")
                || field.getGenericType().toString().equals("char")
                || field.getGenericType().toString().equals("class java.lang.Boolean")
                || field.getGenericType().toString().equals("boolean")) {
            return false;
        }
        return true;
    }

    public static MySqlTypeAndLength getMySqlTypeAndLength(Field field, Class<?> clasz) {
        ColumnType column = AnnotatedElementUtils.findMergedAnnotation(field, ColumnType.class);
        if (column != null && column.value() != MySqlTypeConstant.DEFAULT) {
            return buildMySqlTypeAndLength(clasz, field, column.value().toString().toLowerCase(), column.length(), column.decimalLength());
        }
        // 类型为空根据字段类型去默认匹配类型
        MySqlTypeConstant mysqlType = JavaToMysqlType.getSqlType(field, clasz);
        if (mysqlType == null) {
            throw new RuntimeException("字段名：" + clasz.getName() + ":" + field.getName() + "不支持" + field.getGenericType() + "类型转换到mysql类型，仅支持JavaToMysqlType类中的类型默认转换，异常抛出！");
        }
        String sqlType = mysqlType.toString().toLowerCase();
        // 默认类型可以使用column来设置长度
        if (column != null) {
            return buildMySqlTypeAndLength(clasz, field, sqlType, column.length(), column.decimalLength());
        }
        return buildMySqlTypeAndLength(clasz, field, sqlType, 255, 0);
    }

    private static MySqlTypeAndLength buildMySqlTypeAndLength(Class<?> clasz, Field field, String type, int length, int decimalLength) {

        MySqlTypeAndLength mySqlTypeAndLength = mySqlTypeAndLengthMap.get(type);
        if (mySqlTypeAndLength == null) {
            throw new RuntimeException("字段名：" + clasz.getName() + ":" + field.getName() + "使用的" + type + "类型，没有配置对应的MySqlTypeConstant，只支持创建MySqlTypeConstant中类型的字段，异常抛出！");
        }
        MySqlTypeAndLength targetMySqlTypeAndLength = new MySqlTypeAndLength();
        BeanUtils.copyProperties(mySqlTypeAndLength, targetMySqlTypeAndLength);
        if (length != 255) {
            targetMySqlTypeAndLength.setLength(length);
        }
        if (decimalLength != 0) {
            targetMySqlTypeAndLength.setDecimalLength(decimalLength);
        }
        return targetMySqlTypeAndLength;
    }

    public static boolean hasIgnoreTableAnnotation(Class<?> clasz) {
        return AnnotatedElementUtils.hasAnnotation(clasz, IgnoreTable.class);
    }

    public static boolean isIncloudField(Field field, Class<?> clasz) {

        // 追加逻辑，当TableField(exist=false)的注解修饰时，自动忽略该字段
        TableField tableField = AnnotatedElementUtilsPlus.findMergedAnnotation(field, TableField.class, TableFieldImpl.class);
        if (tableField != null && !tableField.exist()) {
            return false;
        }

        // 不参与建表的字段
        String[] excludeFields = {};
        Table table = AnnotatedElementUtils.findMergedAnnotation(clasz, Table.class);
        if (table != null) {
            excludeFields = table.excludeFields();
        }
        // 当前属性名在排除建表的字段内
        return !Arrays.asList(excludeFields).contains(field.getName());

    }

    /**
     * 添加时间后缀
     *
     * @param tableName 表名
     * @param pattern   时间格式
     * @return
     */
    public static String appendTimeSuffix(String tableName, String pattern) {
        String suffix = "";
        try {
            suffix = DateTimeFormatter.ofPattern(pattern).format(LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException("无法转换时间格式" + pattern);
        }
        return tableName + "_" + suffix;
    }
}
