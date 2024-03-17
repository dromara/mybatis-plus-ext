package com.tangzc.mpe.magic;

import com.baomidou.mybatisplus.core.handlers.AnnotationHandler;
import com.tangzc.mpe.magic.util.AnnotatedElementUtilsPlus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@ConditionalOnMissingBean(AnnotationHandler.class)
public class MyAnnotationHandler implements AnnotationHandler {
    @Override
    public <T extends Annotation> T getAnnotation(Class<?> beanClass, Class<T> annotationClass) {
        return AnnotatedElementUtilsPlus.getDeepMergedAnnotation(beanClass, annotationClass);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Class<?> beanClass, Class<T> annotationClass) {
        return getAnnotation(beanClass, annotationClass) != null;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
        return AnnotatedElementUtilsPlus.getDeepMergedAnnotation(field, annotationClass);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Field field, Class<T> annotationClass) {
        return getAnnotation(field, annotationClass) != null;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        return AnnotatedElementUtilsPlus.getDeepMergedAnnotation(method, annotationClass);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Method method, Class<T> annotationClass) {
        return getAnnotation(method, annotationClass) != null;
    }
}
