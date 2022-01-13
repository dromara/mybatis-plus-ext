package com.tangzc.mpe.actable.manager.handler;

import java.util.Map;
import java.util.Set;

@FunctionalInterface
public interface TableInitHandler {

    void initTable(Map<String, Set<Class<?>>> needCreateTableMap);
}
