package com.tangzc.mpe.actable.manager.handler;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.tangzc.mpe.actable.manager.system.SysMysqlCreateTableManager;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * @author don
 */
public class DynamicDatasourceTableInitHandler implements TableInitHandler {

    @Resource
    private SysMysqlCreateTableManager sysMysqlCreateTableManager;

    @Override
    public void initTable(Map<String, Set<Class<?>>> needCreateTableMap) {

        needCreateTableMap.forEach((dsName, tables) -> {
            // 手动指定数据源
            DynamicDataSourceContextHolder.push(dsName);
            try {
                sysMysqlCreateTableManager.initTable(tables);
            } finally {
                // 移除数据源
                DynamicDataSourceContextHolder.poll();
            }
        });
    }
}
