package com.tangzc.mpe.magic.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class AnnotationDefaultValueHelper {

    public static <A extends Annotation> A getAnnotationWithDefaultValues(Class<A> annotationType) {
        Map<String, Object> defaultValues = getDefaultValues(annotationType);
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
