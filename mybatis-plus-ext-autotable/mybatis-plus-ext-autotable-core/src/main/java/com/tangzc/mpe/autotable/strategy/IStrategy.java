package com.tangzc.mpe.autotable.strategy;

import com.tangzc.mpe.autotable.constants.DatabaseType;
import com.tangzc.mpe.autotable.utils.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author don
 */
public interface IStrategy {

    Logger log = LoggerFactory.getLogger(IStrategy.class);

    /**
     * 开始分析处理模型
     * @param beanClasses 待处理的类
     */
    default void start(Set<Class<?>> beanClasses) {

        // 获取当前数据源所属的方言(目前仅支持MySQL，所以写死了)
        DatabaseType currentDatabaseType = DatabaseType.mysql;

        IStrategy dbStrategy = SpringContextUtil.getBeansOfTypeList(IStrategy.class).stream()
                // 删选出对应方言的策略
                .filter(strategy -> strategy.dbType() == currentDatabaseType).findFirst()
                .orElse(null);
        if (dbStrategy == null) {
            log.warn("没有找到对应的数据库（" + currentDatabaseType + "）方言策略，无法执行自动建表");
        } else {
            dbStrategy.analyseClasses(beanClasses);
        }
    }

    /**
     * 策略对应的数据库方言
     * @return 方言
     */
    DatabaseType dbType();

    /**
     * 分析bean class
     * @param beanClasses 待处理的类
     */
    void analyseClasses(Set<Class<?>> beanClasses);
}
