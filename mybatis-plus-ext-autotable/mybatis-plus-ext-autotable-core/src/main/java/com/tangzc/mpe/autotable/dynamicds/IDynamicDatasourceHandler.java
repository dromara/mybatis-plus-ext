package com.tangzc.mpe.autotable.dynamicds;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.tangzc.mpe.autotable.constants.DatabaseDialect;
import com.tangzc.mpe.autotable.strategy.IStrategy;
import com.tangzc.mpe.autotable.utils.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * @author don
 */
@FunctionalInterface
public interface IDynamicDatasourceHandler {

    Logger log = LoggerFactory.getLogger(IDynamicDatasourceHandler.class);

    /**
     * 开始分析处理模型
     *
     * @param beanClasses 待处理的类
     */
    default void start(Set<Class<?>> beanClasses) {

        DatabaseDialect databaseDialect = this.getDatabaseDialect();
        if (databaseDialect != null) {
            IStrategy<?, ?> databaseStrategy = this.getDatabaseStrategy(databaseDialect);
            if (databaseStrategy != null) {
                databaseStrategy.analyseClasses(beanClasses);
                return;
            }
        }
        log.warn("没有找到对应的数据库（" + databaseDialect + "）方言策略，无法执行自动建表");
    }

    /**
     * 自动获取当前数据源的方言
     *
     * @return 返回数据方言
     */
    default DatabaseDialect getDatabaseDialect() {
        String driverName;
        DataSource dataSource = SpringContextUtil.getBeanOfType(DataSource.class);
        // 多数据源模式
        String dynamicDsClassName = "com.baomidou.dynamic.datasource.DynamicRoutingDataSource";
        if (dynamicDsClassName.equals(dataSource.getClass().getName())) {
            // 上面的if判断只能用字符串，因为如果没有引入类的话，通过类取名字会报错，所以为了防止DynamicRoutingDataSource类的签名有变更，下面通过全名称引用下该类，便于编译期间发现类的签名变了。
            log.info("开启多数据源模式：" + com.baomidou.dynamic.datasource.DynamicRoutingDataSource.class.getName());
            dataSource = ((DynamicRoutingDataSource) dataSource).getDataSource(DynamicDataSourceContextHolder.peek());
            DataSource realDataSource = ((ItemDataSource) dataSource).getRealDataSource();
            try {
                driverName = realDataSource.getConnection().getMetaData().getDriverName();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                driverName = dataSource.getConnection().getMetaData().getDriverName();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        // 获取当前数据源所属的方言
        return DatabaseDialect.parseFromDriverName(driverName);
    }

    /**
     * 获取对应的数据库处理策略
     *
     * @param databaseDialect 数据库方言
     * @return 策略
     */
    default IStrategy<?, ?> getDatabaseStrategy(DatabaseDialect databaseDialect) {
        return (IStrategy<?, ?>) SpringContextUtil.getBeansOfTypeList(IStrategy.class).stream()
                // 删选出对应方言的策略
                .filter(strategy -> strategy.dbDialect() == databaseDialect).findFirst()
                .orElse(null);
    }

    void initTable(Map<String, Set<Class<?>>> needCreateTableMap);
}
