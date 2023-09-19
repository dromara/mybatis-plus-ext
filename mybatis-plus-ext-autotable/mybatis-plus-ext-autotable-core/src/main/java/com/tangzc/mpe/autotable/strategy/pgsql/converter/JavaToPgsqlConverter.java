package com.tangzc.mpe.autotable.strategy.pgsql.converter;

import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlTypeAndLength;

/**
 * @author don
 */
@FunctionalInterface
public interface JavaToPgsqlConverter {

    /**
     * java转pgsql类型
     */
    PgsqlTypeAndLength convert(Class<?> fieldClass);
}
