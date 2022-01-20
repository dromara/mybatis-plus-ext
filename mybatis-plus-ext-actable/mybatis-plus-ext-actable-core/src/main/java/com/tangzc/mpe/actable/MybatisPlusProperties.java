package com.tangzc.mpe.actable;

import org.springframework.beans.factory.annotation.Value;

/**
 * mybatis-plus部分配置
 * @author don
 */
public class MybatisPlusProperties {

    /**
     * 是否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN（下划线命名） 到经典 Java 属性名 aColumn（驼峰命名） 的类似映射。
     * 默认true
     */
    public static boolean mapUnderscoreToCamelCase;
    /**
     * 表名是否使用驼峰转下划线命名,只对表名生效。
     * 默认true
     */
    public static boolean tableUnderline;

    @Value("${mybatis-plus.configuration.map-underscore-to-camel-case:true}")
    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        MybatisPlusProperties.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    @Value("${mybatis-plus.global-config.db-config.table-underline:true}")
    public void setTableUnderline(boolean tableUnderline) {
        MybatisPlusProperties.tableUnderline = tableUnderline;
    }
}
