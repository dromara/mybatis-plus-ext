package com.tangzc.mpe.actable.manager.handler;

import com.tangzc.mpe.actable.manager.system.SysMysqlCreateTableManager;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author don
 */
public class DefaultTableInitHandler implements TableInitHandler {

    @Resource
    private SysMysqlCreateTableManager sysMysqlCreateTableManager;

    @Override
    public void initTable(Map<String, Set<Class<?>>> needCreateTableMap) {

        final Set<Class<?>> tables = needCreateTableMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        sysMysqlCreateTableManager.initTable(tables);
    }
}
