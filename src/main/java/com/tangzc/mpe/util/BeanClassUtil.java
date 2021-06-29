package com.tangzc.mpe.util;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanClassUtil {

    public static <E> String getFieldName(SFunction<E, ?> sf) {
        SerializedLambda lambda = LambdaUtils.resolve(sf);
        return PropertyNamer.methodToProperty(lambda.getImplMethodName());
    }

    public static Map<String, Object> beanToMap(Object bean) {

        Map<String, Object> retMap = new HashMap<>();

        Class<?> beanClass = bean.getClass();
        List<Field> allDeclaredFields = getAllDeclaredFields(beanClass);

        try {
            for (Field field : allDeclaredFields) {
                retMap.put(field.getName(), BeanClassUtil.getReadMethod(beanClass, field).invoke(bean));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

    public static Class<?> getFieldRealClass(Field field) {

        Class<?> fieldClass = field.getType();
        if (Collection.class.isAssignableFrom(fieldClass)) {
            // 如果是集合，获取其泛型参数class
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                fieldClass = (Class<?>) pt.getActualTypeArguments()[0];
            }
        }
        return fieldClass;
    }

    public static <T> Class<?> getFieldRealClass(Class<T> clazz, SFunction<T, ?> sFunction) {

        SerializedLambda lambda = LambdaUtils.resolve(sFunction);
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        Field field;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(clazz + "下未找到" + fieldName + "字段");
        }

        Class<?> fieldClass = field.getType();
        if (Collection.class.isAssignableFrom(fieldClass)) {
            // 如果是集合，获取其泛型参数class
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                fieldClass = (Class<?>) pt.getActualTypeArguments()[0];
            }
        }
        return fieldClass;
    }

    public static Method getWriteMethod(Class<?> clazz, Field field) {

        field.setAccessible(true);
        String fieldName = field.getName();
        return getWriteMethod(clazz, fieldName);
    }

    public static Method getWriteMethod(Class<?> clazz, String fieldName) {

        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(clazz, fieldName);
        if (propertyDescriptor != null && propertyDescriptor.getWriteMethod() != null) {
            return propertyDescriptor.getWriteMethod();
        }

        throw new RuntimeException(clazz + "下未找到" + fieldName + "的set方法");
    }

    public static Method getReadMethod(Class<?> clazz, Field field) {

        field.setAccessible(true);
        String fieldName = field.getName();
        return getReadMethod(clazz, fieldName);
    }

    public static Method getReadMethod(Class<?> clazz, String fieldName) {

        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(clazz, fieldName);
        if (propertyDescriptor != null && propertyDescriptor.getReadMethod() != null) {
            return propertyDescriptor.getReadMethod();
        }

        throw new RuntimeException(clazz + "下未找到" + fieldName + "的get/is方法");
    }

    public static List<Field> getAllDeclaredFields(Class<?> beanClass) {

        List<Field> fieldList = new ArrayList<>();
        getFieldList(fieldList, beanClass);
        return fieldList;
    }

    private static void getFieldList(List<Field> fields, Class<?> beanClass) {

        Field[] declaredFields = beanClass.getDeclaredFields();
        fields.addAll(Arrays.asList(declaredFields));

        Class<?> superclass = beanClass.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            getFieldList(fields, superclass);
        }
    }
}
