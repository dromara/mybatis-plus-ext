package com.tangzc.mpe.autotable.strategy.mysql.converter;

import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlTypeAndLength;

/**
 * @author don
 */
@FunctionalInterface
public interface JavaToMysqlConverter {

    /**
     * java转pgsql类型
     */
    MysqlTypeAndLength convert(Class<?> fieldClass);
}
