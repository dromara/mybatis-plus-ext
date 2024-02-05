package com.tangzc.mpe.autotable;

import com.tangzc.mpe.autotable.dynamicds.define.DefaultTableInitDefine;
import com.tangzc.mpe.autotable.dynamicds.define.DynamicDatasourceTableInitDefine;
import com.tangzc.mpe.autotable.mybatisplus.MybatisPlusIgnore;
import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import com.tangzc.mpe.autotable.strategy.mysql.MysqlStrategy;
import com.tangzc.mpe.autotable.strategy.mysql.converter.impl.DefaultJavaToMysqlConverterDefine;
import com.tangzc.mpe.autotable.strategy.pgsql.PgsqlStrategy;
import com.tangzc.mpe.autotable.strategy.pgsql.converter.impl.DefaultJavaToPgsqlConverterDefine;
import com.tangzc.mpe.autotable.strategy.sqlite.SqliteStrategy;
import com.tangzc.mpe.autotable.strategy.sqlite.converter.impl.DefaultJavaToSqliteConverterDefine;
import com.tangzc.mpe.magic.MybatisPlusProperties;
import com.tangzc.mpe.magic.util.SpringContextUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        SpringContextUtil.class,
        MybatisPlusProperties.class,
        MapperScannerConfig.class,
        MysqlStrategy.class,
        PgsqlStrategy.class,
        SqliteStrategy.class,
        MybatisPlusIgnore.class,
        StartUp.class,
        // 顺序先后 1
        DynamicDatasourceTableInitDefine.class,
        // 顺序先后 2
        DefaultTableInitDefine.class,
        DefaultJavaToMysqlConverterDefine.class,
        DefaultJavaToPgsqlConverterDefine.class,
        DefaultJavaToSqliteConverterDefine.class,
})
@EnableConfigurationProperties(AutoTableProperties.class)
public @interface EnableAutoTable {

    String[] activeProfile() default {};

    String profileProperty() default "spring.profiles.active";
}
