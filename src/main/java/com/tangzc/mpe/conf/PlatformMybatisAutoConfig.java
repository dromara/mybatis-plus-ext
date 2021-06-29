package com.tangzc.mpe.conf;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.tangzc.mpe.core.handler.AutoFillMetaObjectHandler;
import com.tangzc.mpe.common.ApplicationStartListener;
import com.tangzc.mpe.datasource.DataSourceManager;
import com.tangzc.mpe.fixedcondition.FixedConditionInterceptor;
import com.tangzc.mpe.fixedcondition.FixedConditionManager;
import com.tangzc.mpe.util.SpringContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author don
 */
@Configuration
@Import({
        SpringContextUtil.class,
        AutoFillMetaObjectHandler.class,
        ApplicationStartListener.class,
        DataSourceManager.class,
        FixedConditionManager.class,
})
public class PlatformMybatisAutoConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        interceptor.addInnerInterceptor(new FixedConditionInterceptor());
        return interceptor;
    }
}
