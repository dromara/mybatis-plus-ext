package com.tangzc.mpe.base.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import net.sf.jsqlparser.schema.Table;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author don
 */
public class TableColumnUtil {

    private static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");

    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]+");

    public static String getTableName(Table table) {
        return table.getName().replaceAll("`", "");
    }

    public static String getTableName(Class<?> entityClass) {
        String tableName;
        TableName tableNameAnno = AnnotatedElementUtils.findMergedAnnotation(entityClass, TableName.class);
        if (tableNameAnno != null) {
            tableName = tableNameAnno.value();
        } else {
            tableName = TableColumnUtil.humpToLine(entityClass.getSimpleName());
        }
        return tableName.replaceAll("`", "");
    }

    public static String getColumnName(Field field) {
        String columnName;
        TableField annotation = AnnotatedElementUtils.findMergedAnnotation(field, TableField.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            columnName = annotation.value();
        } else {
            columnName = humpToLine(field.getName());
        }
        return columnName;
    }

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {

        str = str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     */
    public static String humpToLine(String str) {

        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
