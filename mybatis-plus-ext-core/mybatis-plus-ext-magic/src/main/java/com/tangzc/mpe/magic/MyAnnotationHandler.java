package com.tangzc.mpe.magic;

import com.baomidou.mybatisplus.core.handlers.AnnotationHandler;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MyAnnotationHandler implements AnnotationHandler {
    @Override
    public <T extends Annotation> T getAnnotation(Class<?> beanClass, Class<T> annotationClass) {
        return AnnotatedElementUtils.getMergedAnnotation(beanClass, annotationClass);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Class<?> beanClass, Class<T> annotationClass) {
        return AnnotatedElementUtils.isAnnotated(beanClass, annotationClass);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
        return AnnotatedElementUtils.getMergedAnnotation(field, annotationClass);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Field field, Class<T> annotationClass) {
        return AnnotatedElementUtils.isAnnotated(field, annotationClass);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        return AnnotatedElementUtils.getMergedAnnotation(method, annotationClass);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Method method, Class<T> annotationClass) {
        return AnnotatedElementUtils.isAnnotated(method, annotationClass);
    }
}
