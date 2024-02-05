package com.tangzc.mpe.magic.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author don
 */
//@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return SpringContextUtil.applicationContext;
    }

    public static String getProperty(String key) {
        return null == applicationContext ? null : applicationContext.getEnvironment().getProperty(key);
    }

    public static String getApplicationName() {
        return getProperty("spring.application.name");
    }

    public static String[] getActiveProfiles() {
        return null == applicationContext ? null : applicationContext.getEnvironment().getActiveProfiles();
    }

    public static String getActiveProfile() {
        String[] activeProfiles = getActiveProfiles();
        if (activeProfiles != null && activeProfiles.length > 0) {
            return activeProfiles[0];
        }
        return null;
    }

    public static void publishEvent(ApplicationEvent event) {
        ApplicationContext context = getApplicationContext();
        if (null != context) {
            context.publishEvent(event);
        }
    }

    public static <T> List<T> getBeansOfTypeList(Class<T> clazz) {

        Map<String, T> beansOfTypeMap = getApplicationContext().getBeansOfType(clazz);
        if (beansOfTypeMap.isEmpty()) {
            return Collections.emptyList();
        }

        return new ArrayList<>(beansOfTypeMap.values());
    }

    public static <T> T getBeanOfType(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static String getBootPackage() {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("main".equals(stackTraceElement.getMethodName())) {
                return ClassUtils.getPackageName(stackTraceElement.getClassName());
            }
        }
        throw new RuntimeException("未找到主默认包");
    }

    public static Class<?> getApplicationClass() {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("main".equals(stackTraceElement.getMethodName())) {
                try {
                    return Class.forName(stackTraceElement.getClassName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("未找到主默认包");
    }
}
