package com.tangzc.mpe.autotable.dynamicds;

import java.util.Map;
import java.util.Set;

/**
 * @author don
 */
@FunctionalInterface
public interface IDynamicDatasourceHandler {

    void initTable(Map<String, Set<Class<?>>> needCreateTableMap);
}
