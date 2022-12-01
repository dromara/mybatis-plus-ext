package com.tangzc.mpe.autotable.properties;

import lombok.Data;

/**
 * 执行sql日志的配置
 */
@Data
public class ExecuteSqlLogProperties {
    /**
     * 开启记录sql日志
     */
    private boolean enable = true;
    /**
     * sql日志记录的表的名字
     */
    private String tableName = "mpe_execute_sql_log";
}
