package com.tangzc.mpe.actable;

import com.tangzc.mpe.actable.mapper.CreateMysqlTablesMapper;
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

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        builder.addPropertyValue("processPropertyPlaceHolders", true);
        builder.addPropertyValue("basePackage", ClassUtils.getPackageName(CreateMysqlTablesMapper.class));

        String beanName = MapperScannerRegistrar.class.getSimpleName() + "#" + MapperScannerConfigurer.class.getSimpleName() + "#0";
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
