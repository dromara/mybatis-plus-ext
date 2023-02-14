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
import com.tangzc.mpe.autotable.utils.SpringContextUtil;
import com.tangzc.mpe.magic.MybatisPlusProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author don
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        // 该类需要排在第一个
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

    String[] activeProfile() default "";

    String profileProperty() default "spring.profiles.active";
}
