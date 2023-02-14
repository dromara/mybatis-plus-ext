package com.tangzc.mpe.autotable.dynamicds.impl;

import com.tangzc.mpe.autotable.dynamicds.IDynamicDatasourceHandler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author don
 */
public class DefaultIDynamicDatasourceHandler implements IDynamicDatasourceHandler {
    
    @Override
    public void initTable(Map<String, Set<Class<?>>> needCreateTableMap) {

        final Set<Class<?>> tables = needCreateTableMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        start(tables);
    }
}
