package com.tangzc.mpe.autotable.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.metadata.AnnotatedElementUtilsPlus;
import com.baomidou.mybatisplus.core.metadata.impl.TableIdImpl;
import com.tangzc.mpe.autotable.annotation.*;
import com.tangzc.mpe.autotable.annotation.impl.*;

import java.lang.reflect.Field;

public class ColumnUtils {

    public static boolean isPrimary(Field field) {

        if (AnnotatedElementUtilsPlus.hasAnnotation(field, TableId.class)) {
            return true;
        }
        Primary column = AnnotatedElementUtilsPlus.findMergedAnnotation(field, Primary.class, PrimaryImpl.class);
        if (column != null && column.value()) {
            return true;
        }

        return "id".equals(field.getName());
    }

    public static boolean isAutoIncrement(Field field) {
        TableId tableId = AnnotatedElementUtilsPlus.findMergedAnnotation(field, TableId.class, TableIdImpl.class);
        return tableId != null && tableId.type() == IdType.AUTO;
    }

    public static Boolean isNotNull(Field field) {
        // 主键默认为非空
        if (isPrimary(field)) {
            return true;
        }

        NotNull column = AnnotatedElementUtilsPlus.findMergedAnnotation(field, NotNull.class, NotNullImpl.class);
        if (column != null) {
            return column.value();
        }
        return false;
    }

    public static String getComment(Field field) {
        ColumnComment column = AnnotatedElementUtilsPlus.findMergedAnnotation(field, ColumnComment.class, ColumnCommentImpl.class);
        if (column != null) {
            return column.value();
        }
        return "";
    }

    public static ColumnDefault getDefaultValue(Field field) {
        return AnnotatedElementUtilsPlus.findMergedAnnotation(field, ColumnDefault.class, ColumnDefaultImpl.class);
    }

    public static ColumnType getColumnType(Field field) {
        return AnnotatedElementUtilsPlus.findMergedAnnotation(field, ColumnType.class, ColumnTypeImpl.class);
    }
}
