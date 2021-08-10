package com.tangzc.mpe.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ContextLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author don
 */
//@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext APPLICATION_CONTEXT;

    /***
     * 获取ApplicationContext上下文
     */
    public static ApplicationContext getApplicationContext() {

        if (APPLICATION_CONTEXT == null) {
            APPLICATION_CONTEXT = ContextLoader.getCurrentWebApplicationContext();
        }
        if (APPLICATION_CONTEXT == null) {
            throw new RuntimeException("无法获取ApplicationContext，请在Spring初始化之后调用!");
        }
        return APPLICATION_CONTEXT;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.APPLICATION_CONTEXT = applicationContext;
    }

    public static <T> List<T> getBeansOfTypeList(Class<T> clazz) {

        Map<String, T> beansOfTypeMap = getApplicationContext().getBeansOfType(clazz);
        if (beansOfTypeMap.isEmpty()) {
            return Collections.emptyList();
        }

        return new ArrayList<>(beansOfTypeMap.values());
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

    public static void publishEvent(ApplicationEvent applicationEvent) {
        APPLICATION_CONTEXT.publishEvent(applicationEvent);
    }
}
