package com.tangzc.mpe.actable.manager.handler;

import com.tangzc.mpe.actable.constants.Constants;
import com.tangzc.mpe.actable.manager.system.SysMysqlCreateTableManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 启动时进行处理的实现类
 *
 * @author chenbin.sun
 */
@Slf4j
public class StartUpHandlerImpl implements StartUpHandler {

    /**
     * 数据库类型：mysql
     */
    public static final String MYSQL = "mysql";

    /**
     * 数据库类型
     */
    @Value(Constants.ACTABLE_DATABASE_TYPE_KEY_VALUE)
    private String databaseType;

    @Resource
    private SysMysqlCreateTableManager sysMysqlCreateTableManager;

    @Override
    @PostConstruct
    public void startHandler() {
        // 执行mysql的处理方法
        if (MYSQL.equals(databaseType)) {
            log.info("databaseType=mysql，开始执行mysql的处理方法");
            sysMysqlCreateTableManager.createMysqlTable();
        } else {
            log.warn("{}数据库暂不支持！无法自动创建表", databaseType);
        }
    }
}
