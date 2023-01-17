package com.tangzc.mpe.autotable.strategy.sqlite.converter;

import com.tangzc.mpe.autotable.strategy.sqlite.data.SqliteTypeAndLength;

/**
 * @author don
 */
@FunctionalInterface
public interface JavaToSqliteConverter {

    /**
     * java转pgsql类型
     */
    SqliteTypeAndLength convert(Class<?> fieldClass);
}
