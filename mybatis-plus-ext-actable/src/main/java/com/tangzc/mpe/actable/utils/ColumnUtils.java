package com.tangzc.mpe.actable.utils;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.CaseFormat;
import com.tangzc.mpe.actable.command.JavaToMysqlType;
import com.tangzc.mpe.actable.command.MySqlTypeAndLength;
import com.tangzc.mpe.annotation.actable.Column;
import com.tangzc.mpe.annotation.actable.EnableTimeSuffix;
import com.tangzc.mpe.annotation.actable.IgnoreTable;
import com.tangzc.mpe.annotation.actable.IsNativeDefValue;
import com.tangzc.mpe.annotation.actable.Table;
import com.tangzc.mpe.annotation.actable.impl.ColumnImpl;
import com.tangzc.mpe.annotation.constants.MySqlCharsetConstant;
import com.tangzc.mpe.annotation.constants.MySqlEngineConstant;
import com.tangzc.mpe.annotation.constants.MySqlTypeConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ColumnUtils {

    public static final String SQL_ESCAPE_CHARACTER = "`";

    /**
     * 获取Mysql的类型，以及类型需要设置几个长度，这里构建成map的样式
     * 构建Map(字段名(小写),需要设置几个长度(0表示不需要设置，1表示需要设置一个，2表示需要设置两个))
     */
    public static Map<String, MySqlTypeAndLength> mySqlTypeAndLengthMap = new HashMap<>();

    /**
     * 缓存Table注解
     */
    public static Map<String, Table> tableMap = new ConcurrentHashMap<>();
    /**
     * 缓存Column注解
     */
    public static Map<String, Column> columnMap = new ConcurrentHashMap<>();

    static {
        for (MySqlTypeConstant type : MySqlTypeConstant.values()) {
            mySqlTypeAndLengthMap.put(type.toString().toLowerCase(), new MySqlTypeAndLength(type.getLengthCount(), type.getLengthDefault(), type.getDecimalLengthDefault(), type.toString().toLowerCase()));
        }
    }

    public static String getTableName(Class<?> clasz) {
        TableName tableNamePlus = AnnotatedElementUtils.findMergedAnnotation(clasz, TableName.class);
        EnableTimeSuffix enableTimeSuffix = AnnotatedElementUtils.findMergedAnnotation(clasz, EnableTimeSuffix.class);
        String finalTableName = "";
        if (tableNamePlus != null && !StringUtils.isEmpty(tableNamePlus.value())) {
            finalTableName = tableNamePlus.value();
        }
        if (StringUtils.isEmpty(finalTableName)) {
            // 都为空时采用类名按照驼峰格式转会为表名
            finalTableName = getBuildLowerName(clasz.getSimpleName());
        }
        if (null != enableTimeSuffix && enableTimeSuffix.value()) {
            finalTableName = appendTimeSuffix(finalTableName, enableTimeSuffix.pattern());
        }
        return finalTableName;
    }

    public static String getTableComment(Class<?> clasz) {
        Table table = getTable(clasz);
        if (table != null) {
            return table.comment();
        }
        return "";
    }

    public static MySqlCharsetConstant getTableCharset(Class<?> clasz) {
        Table table = getTable(clasz);
        if (table != null && table.charset() != MySqlCharsetConstant.DEFAULT) {
            return table.charset();
        }
        return null;
    }

    public static MySqlEngineConstant getTableEngine(Class<?> clasz) {
        Table table = getTable(clasz);
        if (table != null && table.engine() != MySqlEngineConstant.DEFAULT) {
            return table.engine();
        }
        return null;
    }

    public static String getColumnName(Field field) {
        TableField tableField = AnnotatedElementUtils.findMergedAnnotation(field, TableField.class);
        if (tableField != null && !StringUtils.isEmpty(tableField.value()) && tableField.exist()) {
            return tableField.value().toLowerCase().replace(SQL_ESCAPE_CHARACTER, "");
        }
        TableId tableId = AnnotatedElementUtils.findMergedAnnotation(field, TableId.class);
        if (tableId != null && !StringUtils.isEmpty(tableId.value())) {
            return tableId.value().replace(SQL_ESCAPE_CHARACTER, "");
        }
        return getBuildLowerName(field.getName()).replace(SQL_ESCAPE_CHARACTER, "");
    }

    private static String getBuildLowerName(String name) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                name).toLowerCase();
    }

    public static boolean isKey(Field field, Class<?> clasz) {
        if (!hasColumnAnnotation(field, clasz)) {
            return false;
        }
        Column column = getColumn(field, clasz);
        if (column != null && column.isKey()) {
            return true;
        }
        if (AnnotatedElementUtils.hasAnnotation(field, TableId.class)) {
            return true;
        }

        return "id".equals(field.getName());
    }

    public static boolean isAutoIncrement(Field field, Class<?> clasz) {
        Column column = getColumn(field, clasz);
        if (!hasColumnAnnotation(field, clasz)) {
            return false;
        }
        if (column != null && column.isAutoIncrement()) {
            return true;
        }
        return false;
    }

    public static Boolean isNull(Field field, Class<?> clasz) {
        // 主键默认为非空
        if (isKey(field, clasz)) {
            return false;
        }

        Column column = getColumn(field, clasz);
        if (column != null) {
            return column.isNull();
        }
        return true;
    }

    public static String getComment(Field field, Class<?> clasz) {
        Column column = getColumn(field, clasz);
        if (column != null) {
            return column.comment();
        }
        return "";
    }

    public static String getDefaultValue(Field field, Class<?> clasz) {
        Column column = getColumn(field, clasz);
        if (column != null && !"".equals(column.defaultValue())) {
            return column.defaultValue();
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
        Column column = getColumn(field, clasz);
        if (column != null && column.type() != MySqlTypeConstant.DEFAULT) {
            return buildMySqlTypeAndLength(field, column.type().toString().toLowerCase(), column.length(), column.decimalLength());
        }
        // 类型为空根据字段类型去默认匹配类型
        MySqlTypeConstant mysqlType = JavaToMysqlType.getSqlType(field, clasz);
        if (mysqlType == null) {
            throw new RuntimeException("字段名：" + field.getName() + "不支持" + field.getGenericType() + "类型转换到mysql类型，仅支持JavaToMysqlType类中的类型默认转换，异常抛出！");
        }
        String sqlType = mysqlType.toString().toLowerCase();
        // 默认类型可以使用column来设置长度
        if (column != null) {
            return buildMySqlTypeAndLength(field, sqlType, column.length(), column.decimalLength());
        }
        return buildMySqlTypeAndLength(field, sqlType, 255, 0);
    }

    private static MySqlTypeAndLength buildMySqlTypeAndLength(Field field, String type, int length, int decimalLength) {

        MySqlTypeAndLength mySqlTypeAndLength = mySqlTypeAndLengthMap.get(type);
        if (mySqlTypeAndLength == null) {
            throw new RuntimeException("字段名：" + field.getName() + "使用的" + type + "类型，没有配置对应的MySqlTypeConstant，只支持创建MySqlTypeConstant中类型的字段，异常抛出！");
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

    public static boolean hasColumnAnnotation(Field field, Class<?> clasz) {
        // 不参与建表的字段
        String[] excludeFields = excludeFields(clasz);
        // 当前属性名在排除建表的字段内
        return !Arrays.asList(excludeFields).contains(field.getName());

    }

    private static Column getColumn(Field field, Class<?> clasz) {
        // 不参与建表的字段
        String[] excludeFields = excludeFields(clasz);
        if (Arrays.asList(excludeFields).contains(field.getName())) {
            return null;
        }
        Column column = columnMap.computeIfAbsent(clasz.getName() + ":" + field.getName(), k -> AnnotatedElementUtils.findMergedAnnotation(field, Column.class));
        return column != null ? column : new ColumnImpl();
    }

    private static Table getTable(Class<?> clasz) {

        return tableMap.computeIfAbsent(clasz.getName(), k -> AnnotatedElementUtils.findMergedAnnotation(clasz, Table.class));
    }

    private static String[] excludeFields(Class<?> clasz) {
        String[] excludeFields = {};
        Table table = getTable(clasz);
        if (table != null) {
            excludeFields = table.excludeFields();
        }

        // 追加逻辑，当TableField(exist=false)的注解修饰时，自动忽略该字段
        Field[] fields = clasz.getDeclaredFields();
        List<String> fieldList = Arrays.stream(fields)
                .filter(field -> {
                    TableField tableField = AnnotationUtils.findAnnotation(field, TableField.class);
                    return tableField != null && !tableField.exist();
                })
                .map(Field::getName)
                .collect(Collectors.toList());
        if (!fieldList.isEmpty()) {
            if (excludeFields.length > 0) {
                fieldList.addAll(Arrays.asList(excludeFields));
            }
            return fieldList.toArray(new String[0]);
        }

        return excludeFields;
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
