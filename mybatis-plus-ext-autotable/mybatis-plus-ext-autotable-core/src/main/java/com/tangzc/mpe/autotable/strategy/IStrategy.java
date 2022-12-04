package com.tangzc.mpe.autotable.strategy;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.tangzc.mpe.autotable.constants.DatabaseDialect;
import com.tangzc.mpe.autotable.constants.RunMode;
import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import com.tangzc.mpe.autotable.utils.SpringContextUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author don
 */
public interface IStrategy<TABLE_META extends TableMetadata, COMPARE_TABLE_INFO> {

    Logger log = LoggerFactory.getLogger(IStrategy.class);

    /**
     * 开始分析处理模型
     *
     * @param beanClasses 待处理的类
     */
    default void start(Set<Class<?>> beanClasses) {

        DatabaseDialect databaseDialect = getDatabaseDialect();
        IStrategy<?, ?> databaseStrategy = getDatabaseStrategy(databaseDialect);
        if (databaseStrategy == null) {
            log.warn("没有找到对应的数据库（" + databaseDialect + "）方言策略，无法执行自动建表");
        } else {
            databaseStrategy.analyseClasses(beanClasses);
        }
    }

    /**
     * 获取对应的数据库处理策略
     *
     * @param databaseDialect 数据库方言
     * @return 策略
     */
    @Nullable
    default IStrategy<?, ?> getDatabaseStrategy(DatabaseDialect databaseDialect) {
        return (IStrategy<?, ?>) SpringContextUtil.getBeansOfTypeList(IStrategy.class).stream()
                // 删选出对应方言的策略
                .filter(strategy -> strategy.dbDialect() == databaseDialect).findFirst()
                .orElse(null);
    }

    /**
     * 自动获取当前数据源的方言
     *
     * @return 返回数据方言
     */
    @Nullable
    default DatabaseDialect getDatabaseDialect() {
        String driverName;
        DataSource dataSource = SpringContextUtil.getBeanOfType(DataSource.class);
        // 多数据源模式
        String dynamicDsClassName = "com.baomidou.dynamic.datasource.DynamicRoutingDataSource";
        if (dynamicDsClassName.equals(dataSource.getClass().getName())) {
            // 纯属为了校验，防止DynamicRoutingDataSource类的签名有变更
            assert com.baomidou.dynamic.datasource.DynamicRoutingDataSource.class.getName().equals(dynamicDsClassName);
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
     * 策略对应的数据库方言
     *
     * @return 方言
     */
    DatabaseDialect dbDialect();

    /**
     * 分析bean class
     *
     * @param beanClasses 待处理的类
     */
    default void analyseClasses(Set<Class<?>> beanClasses) {

        for (Class<?> beanClass : beanClasses) {

            TABLE_META tableMetadata = analyseClass(beanClass);

            // 构建数据模型失败跳过
            if (tableMetadata == null) {
                continue;
            }

            String tableName = tableMetadata.getTableName();
            AutoTableProperties autoTableProperties = SpringContextUtil.getBeanOfType(AutoTableProperties.class);
            if (autoTableProperties.getMode() == RunMode.create) {
                // create模式特殊对待，如果配置文件配置的是create，表示将所有的表删掉重新创建
                log.info("由于配置的模式是create，因此先删除表后续根据结构重建，删除表：{}", tableName);
                // 直接删除表重新生成
                dropTable(tableName);
            }

            // 判断表是否存在
            boolean isExist = checkTableExist(tableName);
            if (isExist) {
                // 当表存在，比对表与Bean描述的差异
                COMPARE_TABLE_INFO compareTableInfo = compareTable(tableMetadata);
                // 修改表信息
                modifyTable(compareTableInfo);
            } else {
                // 当表不存在的时候，直接生成表
                createTable(tableMetadata);
            }
        }
    }

    /**
     * 根据表名删除表
     *
     * @param tableName 表名
     */
    void dropTable(String tableName);

    /**
     * 检查表是否存在
     *
     * @param tableName 表名
     * @return 表详情
     */
    boolean checkTableExist(String tableName);

    /**
     * 分析Bean，得到元数据信息
     *
     * @param beanClass 待分析的class
     * @return 表元信息
     */
    TABLE_META analyseClass(Class<?> beanClass);

    /**
     * 创建表
     *
     * @param tableMetadata 表元数据
     */
    void createTable(TABLE_META tableMetadata);

    /**
     * 对比表与bean的差异
     *
     * @param tableMetadata 表元数据
     * @return 待修改的表信息描述
     */
    COMPARE_TABLE_INFO compareTable(TABLE_META tableMetadata);

    /**
     * 修改表
     *
     * @param compareTableInfo 修改表的描述信息
     */
    void modifyTable(COMPARE_TABLE_INFO compareTableInfo);
}
