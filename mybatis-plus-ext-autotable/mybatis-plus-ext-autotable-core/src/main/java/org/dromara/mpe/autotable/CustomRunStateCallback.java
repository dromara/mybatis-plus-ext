package org.dromara.mpe.autotable;

import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import org.dromara.autotable.core.callback.RunAfterCallback;
import org.dromara.autotable.core.callback.RunBeforeCallback;

/**
 * 多数据源模式
 *
 * @author don
 */
public class CustomRunStateCallback {

    public static class CustomRunBeforeCallback implements RunBeforeCallback {
        @Override
        public void before(Class<?> tableClass) {
            // 设置忽略租户插件 tenantLine = "true", illegalSql = "true", blockAttack = "true"
            InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).illegalSql(true).blockAttack(true).build());
        }
    }

    public static class CustomRunAfterCallback implements RunAfterCallback {
        @Override
        public void after(Class<?> tableClass) {
            // 关闭忽略策略
            InterceptorIgnoreHelper.clearIgnoreStrategy();
        }
    }
}
