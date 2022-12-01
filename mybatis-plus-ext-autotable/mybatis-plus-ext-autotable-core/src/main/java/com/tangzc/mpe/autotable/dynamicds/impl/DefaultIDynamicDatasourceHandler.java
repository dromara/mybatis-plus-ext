package com.tangzc.mpe.autotable.dynamicds.impl;

import com.tangzc.mpe.autotable.dynamicds.IDynamicDatasourceHandler;
import com.tangzc.mpe.autotable.strategy.mysql.MysqlStrategy;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author don
 */
public class DefaultIDynamicDatasourceHandler implements IDynamicDatasourceHandler {

    @Resource
    private MysqlStrategy mysqlStrategy;

    @Override
    public void initTable(Map<String, Set<Class<?>>> needCreateTableMap) {

        final Set<Class<?>> tables = needCreateTableMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        mysqlStrategy.start(tables);
    }
}
