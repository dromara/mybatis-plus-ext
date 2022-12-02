package com.tangzc.mpe.autotable.strategy;

import com.tangzc.mpe.autotable.constants.DatabaseType;
import com.tangzc.mpe.autotable.constants.RunMode;
import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import com.tangzc.mpe.autotable.utils.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author don
 */
public interface IStrategy<TABLE_META extends TableMetadata, COMPARE_TABLE_INFO, TABLE_INFO> {

    Logger log = LoggerFactory.getLogger(IStrategy.class);

    /**
     * 开始分析处理模型
     *
     * @param beanClasses 待处理的类
     */
    default void start(Set<Class<?>> beanClasses) {

        // 获取当前数据源所属的方言(目前仅支持MySQL，所以写死了)
        DatabaseType currentDatabaseType = DatabaseType.mysql;

        IStrategy<?, ?, ?> dbStrategy = SpringContextUtil.getBeansOfTypeList(IStrategy.class).stream()
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
     *
     * @return 方言
     */
    DatabaseType dbType();

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
            TABLE_INFO tableInfo = getTableInformationFromDb(tableName);
            if (tableInfo == null) {
                // 当表不存在的时候，直接生成表
                createTable(tableMetadata);
            } else {
                // 当表存在，比对表与Bean描述的差异
                COMPARE_TABLE_INFO compareTableInfo = compareTable(tableMetadata, tableInfo);

                // 修改表信息
                modifyTable(compareTableInfo);
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
     * 根据表名查询表详情
     *
     * @param tableName 表名
     * @return 表详情
     */
    TABLE_INFO getTableInformationFromDb(String tableName);

    /**
     * 分析Bean，得到元数据信息
     *
     * @param beanClass 待分析的class
     * @return 表元信息
     */
    TABLE_META analyseClass(Class<?> beanClass);

    /**
     * 创建表
     * @param tableMetadata 表元数据
     */
    void createTable(TABLE_META tableMetadata);

    /**
     * 对比表与bean的差异
     *
     * @param tableMetadata 表元数据
     * @param tableInfo     数据库表信息
     * @return 待修改的表信息描述
     */
    COMPARE_TABLE_INFO compareTable(TABLE_META tableMetadata, TABLE_INFO tableInfo);

    /**
     * 修改表
     *
     * @param compareTableInfo 修改表的描述信息
     */
    void modifyTable(COMPARE_TABLE_INFO compareTableInfo);
}
