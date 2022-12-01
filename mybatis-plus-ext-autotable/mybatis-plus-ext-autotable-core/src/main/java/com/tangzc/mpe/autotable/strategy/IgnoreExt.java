package com.tangzc.mpe.autotable.strategy;

import java.lang.reflect.Field;

/**
 * @author don
 */
public interface IgnoreExt {

    /**
     * 拓展判断是否是忽略的字段
     * @param field 当前字段
     * @param clazz 当前字段class
     * @return 结果
     */
    boolean isIgnoreField(Field field, Class<?> clazz);
}
