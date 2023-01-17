package com.tangzc.mpe.autotable.strategy.mysql;

import com.tangzc.mpe.autotable.annotation.ColumnType;
import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlColumnMetadata;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlTypeAndLength;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * 参数校验器
 * @author don
 */
public class ParamValidChecker {

    /**
     * 自增与类型的匹配校验
     */
    private static final IColumnChecker CHECK_AUTO_INCREMENT = (clazz, field, columnParam) -> {
        MysqlTypeAndLength columnType = columnParam.getType();
        if (columnParam.isAutoIncrement() && !columnType.isNumber()) {
            return new RuntimeException(String.format("类(%s)的字段(%s[%s])设置了自增，但是匹配到的(%s)非数字类型，无法自增，请尝试通过@%s手动指定数据库类型或更换类的字段类型",
                    clazz.getName(), field.getName(), field.getType().getName(), columnType.typeName(), ColumnType.class.getName()));
        }
        return null;
    };
    /**
     * 空字符默认值，只能在字段类型为字符串的时候设置
     */
    private static final IColumnChecker CHECK_DEFAULT_IS_EMPTY_STRING = (clazz, field, columnParam) -> {
        MysqlTypeAndLength columnType = columnParam.getType();
        boolean defaultIsEmptyString = columnParam.getDefaultValueType() == DefaultValueEnum.EMPTY_STRING;
        if (defaultIsEmptyString && !columnType.isCharString()) {
            return new RuntimeException(String.format("类(%s)的字段(%s[%s])设置了默认值为空字符，但是匹配到的(%s)非字符类型，请尝试通过@%s手动指定数据库类型或更换类的字段类型",
                    clazz.getName(), field.getName(), field.getType().getName(), columnType.typeName(), ColumnType.class.getName()));
        }
        return null;
    };

    private static final List<IColumnChecker> COLUMN_PARAM_CHECKER_LIST = Arrays.asList(CHECK_AUTO_INCREMENT, CHECK_DEFAULT_IS_EMPTY_STRING);

    public static void checkColumnParam(Class<?> clazz, Field field, MysqlColumnMetadata mysqlColumnMetadata) {
        for (IColumnChecker iColumnChecker : COLUMN_PARAM_CHECKER_LIST) {
            iColumnChecker.check(clazz, field, mysqlColumnMetadata);
        }
    }

    /**
     * 字段参数校验
     */
    @FunctionalInterface
    public static interface IColumnChecker {
        /**
         * 校验
         */
        Exception check(Class<?> clazz, Field field, MysqlColumnMetadata mysqlColumnMetadata) throws RuntimeException;
    }
}
