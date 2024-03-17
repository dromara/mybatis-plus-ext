package com.tangzc.mpe.magic.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class AnnotationDefaultValueHelper {

    public static <A extends Annotation> A getAnnotationWithDefaultValues(Class<A> annotationType, Consumer<Map<String, Object>> changeDefaultValues) {
        Map<String, Object> defaultValues = getDefaultValues(annotationType);
        if (changeDefaultValues != null) {
            changeDefaultValues.accept(defaultValues);
        }
        return createAnnotationInstance(annotationType, defaultValues);
    }

    public static <A extends Annotation> Map<String, Object> getDefaultValues(Class<A> annotationType) {
        Map<String, Object> defaultValues = new HashMap<>();
        Method[] declaredMethods = annotationType.getDeclaredMethods();

        for (Method method : declaredMethods) {
            if (method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                Object defaultValue = method.getDefaultValue();
                defaultValues.put(method.getName(), defaultValue);
            }
        }

        return defaultValues;
    }

    /**
     * 获取类注解属性
     */
    public static void setAnnoVal(Annotation annotation, String fieldName, Object newVal) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        try {
            Field valuesField = invocationHandler.getClass().getDeclaredField("valueCache");
            valuesField.setAccessible(true);
            Map<String, Object> memberValues = (Map<String, Object>) valuesField.get(invocationHandler);
            memberValues.put(fieldName, newVal);
        } catch (Exception e) {
            log.error("注解反射出错", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A createAnnotationInstance(Class<A> annotationType, Map<String, Object> values) {
        return (A) Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[]{annotationType}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                if (values.containsKey(methodName)) {
                    return values.get(methodName);
                }
                return method.invoke(this, args);
            }
        });
    }
}
