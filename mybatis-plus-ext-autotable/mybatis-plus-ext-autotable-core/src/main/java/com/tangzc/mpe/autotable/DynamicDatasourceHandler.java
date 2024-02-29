package com.tangzc.mpe.autotable;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.tangzc.autotable.core.dynamicds.IDataSourceHandler;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;

import javax.annotation.Resource;

/**
 * 多数据源模式
 *
 * @author don
 */
@Slf4j
public class DynamicDatasourceHandler implements IDataSourceHandler<String> {
    @Resource
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Override
    public void useDataSource(String dsName) {
        // 设置数据源
        DynamicDataSourceContextHolder.push(dsName);
        // 设置忽略租户插件 tenantLine = "true", illegalSql = "true", blockAttack = "true"
        InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).illegalSql(true).blockAttack(true).build());
    }

    @Override
    public void clearDataSource(String serializable) {
        // 清空数据源配置
        DynamicDataSourceContextHolder.poll();
        // 关闭忽略策略
        InterceptorIgnoreHelper.clearIgnoreStrategy();
    }

    @NonNull
    @Override
    public String getDataSourceName(Class clazz) {

        Table tableAnno = AnnotatedElementUtils.findMergedAnnotation(clazz, Table.class);
        if (tableAnno != null) {
            return tableAnno.dsName();
        }
        return dynamicDataSourceProperties.getPrimary();
    }
}
