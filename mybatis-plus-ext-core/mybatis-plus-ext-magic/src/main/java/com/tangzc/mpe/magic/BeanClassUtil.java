package com.tangzc.mpe.magic;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class BeanClassUtil {

    public static <E> String getFieldName(SFunction<E, ?> sf) {
        LambdaMeta lambda = LambdaUtils.extract(sf);
        return PropertyNamer.methodToProperty(lambda.getImplMethodName());
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

        String fieldName = getFieldName(sFunction);
        Field field = getField(clazz, fieldName);
        if (field == null) {
            throw new RuntimeException(clazz + "下未找到" + fieldName + "字段");
        }

        return getFieldRealClass(field);
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

    /**
     * 查找类下指定的字段，如果当前类没有，那就去它的父类寻找
     * @param clazz 类
     * @param fieldName 字段名
     * @return 字段
     */
    public static Field getField(Class<?> clazz, String fieldName) {

        while (true) {
            Field field = Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> f.getName().equals(fieldName))
                    .findFirst().orElse(null);
            // 如果没有找到
            if (field == null) {
                Class<?> superclass = clazz.getSuperclass();
                // 如果存在父类，且不是Object，则去上一级的父类继续寻找
                if (superclass != null && superclass != Object.class) {
                    clazz = superclass;
                    continue;
                }
            }
            return field;
        }
    }

    public static List<Field> getAllDeclaredFields(Class<?> beanClass) {

        List<Field> fieldList = new ArrayList<>();
        getFieldList(fieldList, beanClass);
        return fieldList;
    }

    private static void getFieldList(List<Field> fields, Class<?> beanClass) {

        Field[] declaredFields = beanClass.getDeclaredFields();
        // 获取当前class的所有fields的name列表
        Set<String> fieldNames = fields.stream().map(Field::getName).collect(Collectors.toSet());
        for (Field field : declaredFields) {
            // 避免重载属性
            if (fieldNames.contains(field.getName())) {
                continue;
            }
            fields.add(field);
        }

        Class<?> superclass = beanClass.getSuperclass();
        if (superclass != null) {
            getFieldList(fields, superclass);
        }
    }
}
