package org.dromara.mpe.magic;

import org.springframework.beans.factory.annotation.Value;

/**
 * mybatis-plus部分配置
 *
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
    /**
     * 表名前缀
     */
    public static String tablePrefix;

    /**
     * 表名前缀
     */
    public static boolean capitalMode;

    @Value("${mybatis-plus.configuration.map-underscore-to-camel-case:true}")
    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        MybatisPlusProperties.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    @Value("${mybatis-plus.global-config.db-config.table-underline:true}")
    public void setTableUnderline(boolean tableUnderline) {
        MybatisPlusProperties.tableUnderline = tableUnderline;
    }

    @Value("${mybatis-plus.global-config.db-config.table-prefix:}")
    public void setTablePrefix(String tablePrefix) {
        MybatisPlusProperties.tablePrefix = tablePrefix;
    }

    @Value("${mybatis-plus.global-config.db-config.capital-mode:false}")
    public static void setCapitalMode(boolean capitalMode) {
        MybatisPlusProperties.capitalMode = capitalMode;
    }
}
