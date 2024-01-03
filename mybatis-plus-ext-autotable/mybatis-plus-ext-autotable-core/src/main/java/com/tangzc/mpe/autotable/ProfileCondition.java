package com.tangzc.mpe.autotable;

import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Slf4j
public class ProfileCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        EnableAutoTable enableAutoTable = null;
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        if (beanFactory != null) {
            enableAutoTable = beanFactory.getBeansWithAnnotation(EnableAutoTable.class).values().stream().findFirst()
                    .map(Object::getClass)
                    .map(clazz -> clazz.getAnnotation(EnableAutoTable.class))
                    .orElse(null);
        }

        if (enableAutoTable == null) {
            log.error("未找到EnableAutoTable声明，无法自动建表");
            return false;
        }

        /* 注解上有配置，优先注解的配置逻辑；注解没有配置，则判断配置文件的配置 */
        // 1、默认情况，没有通过注解配置
        boolean isDefault = enableAutoTable.activeProfile().length == 0;
        if(isDefault) {
            // 判断配置文件的配置
            Boolean enable = context.getEnvironment().getProperty(AutoTableProperties.ENABLE_KEY, Boolean.class);
            return enable == null || enable;
        }

        // 2、检查注解的配置
        String[] properties = context.getEnvironment().getProperty(enableAutoTable.profileProperty(), String[].class);
        if (properties != null && properties.length > 0) {
            Set<String> propertySet = Arrays.stream(properties).collect(Collectors.toSet());
            return Arrays.stream(enableAutoTable.activeProfile()).anyMatch(propertySet::contains);
        } else {
            throw new RuntimeException("auto-table缺少配置项：" + enableAutoTable.profileProperty());
        }
    }
}
