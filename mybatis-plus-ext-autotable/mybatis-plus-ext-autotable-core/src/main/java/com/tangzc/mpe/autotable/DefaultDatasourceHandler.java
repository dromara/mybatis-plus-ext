package com.tangzc.mpe.autotable;

import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.tangzc.autotable.core.dynamicds.IDataSourceHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * 多数据源模式
 *
 * @author don
 */
@Slf4j
@ConditionalOnMissingBean(IDataSourceHandler.class)
public class DefaultDatasourceHandler implements IDataSourceHandler<String> {

    @Override
    public void useDataSource(String dsName) {
        // 设置忽略租户插件 tenantLine = "true", illegalSql = "true", blockAttack = "true"
        InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).illegalSql(true).blockAttack(true).build());
    }

    @Override
    public void clearDataSource(String serializable) {
        // 关闭忽略策略
        InterceptorIgnoreHelper.clearIgnoreStrategy();
    }

    @NonNull
    @Override
    public String getDataSourceName(Class clazz) {
        return "";
    }
}
