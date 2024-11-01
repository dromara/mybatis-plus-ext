package org.dromara.mpe.autotable;

import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.tangzc.autotable.core.callback.RunStateCallback;
import lombok.extern.slf4j.Slf4j;

/**
 * 多数据源模式
 *
 * @author don
 */
@Slf4j
public class CustomRunStateCallback implements RunStateCallback {

    @Override
    public void before(Class<?> tableClass) {
        // 设置忽略租户插件 tenantLine = "true", illegalSql = "true", blockAttack = "true"
        InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).illegalSql(true).blockAttack(true).build());
    }

    @Override
    public void after(Class<?> tableClass) {
        // 关闭忽略策略
        InterceptorIgnoreHelper.clearIgnoreStrategy();
    }
}
