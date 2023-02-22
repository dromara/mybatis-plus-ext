package com.tangzc.mpe.autotable.dynamicds.impl;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.tangzc.mpe.autotable.dynamicds.IDynamicDatasourceHandler;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * @author don
 */
public class DynamicDatasourceIDynamicDatasourceHandler implements IDynamicDatasourceHandler {

    @Resource
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Override
    public void initTable(Map<String, Set<Class<?>>> needCreateTableMap) {

        needCreateTableMap.forEach((dsName, tables) -> {
            // 手动指定数据源
            if(StringUtils.isEmpty(dsName)) {
                dsName = dynamicDataSourceProperties.getPrimary();
            }
            DynamicDataSourceContextHolder.push(dsName);
            try {
                this.start(tables);
            } finally {
                // 移除数据源
                DynamicDataSourceContextHolder.poll();
            }
        });
    }
}
