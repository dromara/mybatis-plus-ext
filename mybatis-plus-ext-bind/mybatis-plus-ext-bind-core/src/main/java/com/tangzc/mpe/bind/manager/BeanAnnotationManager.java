package com.tangzc.mpe.bind.manager;

import com.tangzc.mpe.bind.metadata.BeanDescription;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 所有注册过Bind*的Bean的缓存
 *
 * @author don
 */
public class BeanAnnotationManager {

    public static final Map<String, BeanDescription<?>> BEAN_ANNOTATION_CACHE_MAP = new ConcurrentHashMap<>();

    public static <BEAN> BeanDescription<BEAN> getBeanAnnotation(Class<BEAN> beanClass, List<String> includeField, List<String> ignoreField) {

        // 因为一个class可能存在多用关联用法，所以，用复合字符串作为key标识
        String[] keys = {beanClass.getName(), String.join(",", includeField), String.join(",", ignoreField)};
        String key = String.join("#", keys);

        return (BeanDescription<BEAN>) BEAN_ANNOTATION_CACHE_MAP.computeIfAbsent(key,
                k -> BeanAnnotationManager.initBeanAnnotation(beanClass, includeField, ignoreField));
    }

    private static <BEAN> BeanDescription<BEAN> initBeanAnnotation(Class<BEAN> beanClass, List<String> includeField, List<String> ignoreField) {

        BeanDescription<BEAN> beanAnnotation = new BeanDescription<>(beanClass);
        List<Field> fieldList = extractAllFields(beanClass);
        Set<String> ignoreFieldSet = new HashSet<>(ignoreField);
        Set<String> includeFieldSet = new HashSet<>(includeField);
        for (Field field : fieldList) {
            // 跳过不被包含的字段
            if (!includeFieldSet.isEmpty() && !includeFieldSet.contains(field.getName())) {
                continue;
            }

            // 跳过忽略的字段
            if (includeFieldSet.isEmpty() && ignoreFieldSet.contains(field.getName())) {
                continue;
            }

            for (Annotation annotation : field.getDeclaredAnnotations()) {
                beanAnnotation.tryAddBindAnnotation(field, annotation);
            }
        }
        return beanAnnotation;
    }

    /**
     * 递归获取bean上所有字段，包括父类的
     */
    private static List<Field> extractAllFields(Class<?> clazz) {

        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldNameSet = new HashSet<>();
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            //被重写属性，以子类override的为准
            Arrays.stream(fields).forEach((field) -> {
                if (!fieldNameSet.contains(field.getName())) {
                    fieldList.add(field);
                    fieldNameSet.add(field.getName());
                }
            });
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }
}
