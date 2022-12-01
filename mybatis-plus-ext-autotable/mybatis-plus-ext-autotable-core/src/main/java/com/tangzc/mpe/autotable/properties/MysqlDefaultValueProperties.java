package com.tangzc.mpe.autotable.properties;

import lombok.Data;

/**
 * MySQL默认值的相关配置
 * 用于如下几种情况：
 * 1、在类注解未指定数据库相关的配置的情况下，但是数据库需要一个默认值；
 * 2、在类注解未指定数据库相关的配置的情况下，数据库会自动生成一个默认值，但是做模型与表的比对的时候，会分不清要不要删除掉数据库中的
 */
@Data
public class MysqlDefaultValueProperties {
    /**
     * 开启记录sql日志
     */
    private boolean enable = true;
}
