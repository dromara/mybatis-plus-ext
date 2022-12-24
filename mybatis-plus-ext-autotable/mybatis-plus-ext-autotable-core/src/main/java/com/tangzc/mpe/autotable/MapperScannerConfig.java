package com.tangzc.mpe.autotable;

import com.tangzc.mpe.autotable.strategy.mysql.mapper.MysqlTablesMapper;
import com.tangzc.mpe.autotable.strategy.pgsql.mapper.PgsqlTablesMapper;
import com.tangzc.mpe.autotable.strategy.sqlite.mapper.SqliteTablesMapper;
import org.mybatis.spring.annotation.MapperScannerRegistrar;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.util.ClassUtils;

/**
 * 自动注册自定义的 mapper接口
 * @author don
 */
public class MapperScannerConfig implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        // 注册mysql的mapper
        BeanDefinitionBuilder mysqlMapperBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        mysqlMapperBuilder.addPropertyValue("processPropertyPlaceHolders", true);
        mysqlMapperBuilder.addPropertyValue("basePackage", ClassUtils.getPackageName(MysqlTablesMapper.class));
        String mysqlMapperBeanName = MapperScannerRegistrar.class.getSimpleName() + "#" + MapperScannerConfigurer.class.getSimpleName() + "#0";
        registry.registerBeanDefinition(mysqlMapperBeanName, mysqlMapperBuilder.getBeanDefinition());

        // 注册pgsql的mapper
        BeanDefinitionBuilder pgsqlMapperBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        pgsqlMapperBuilder.addPropertyValue("processPropertyPlaceHolders", true);
        pgsqlMapperBuilder.addPropertyValue("basePackage", ClassUtils.getPackageName(PgsqlTablesMapper.class));
        String pgsqlMapperBeanName = MapperScannerRegistrar.class.getSimpleName() + "#" + MapperScannerConfigurer.class.getSimpleName() + "#1";
        registry.registerBeanDefinition(pgsqlMapperBeanName, pgsqlMapperBuilder.getBeanDefinition());

        // 注册sqlite的mapper
        BeanDefinitionBuilder sqliteMapperBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        sqliteMapperBuilder.addPropertyValue("processPropertyPlaceHolders", true);
        sqliteMapperBuilder.addPropertyValue("basePackage", ClassUtils.getPackageName(SqliteTablesMapper.class));
        String sqliteMapperBeanName = MapperScannerRegistrar.class.getSimpleName() + "#" + MapperScannerConfigurer.class.getSimpleName() + "#1";
        registry.registerBeanDefinition(sqliteMapperBeanName, sqliteMapperBuilder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
