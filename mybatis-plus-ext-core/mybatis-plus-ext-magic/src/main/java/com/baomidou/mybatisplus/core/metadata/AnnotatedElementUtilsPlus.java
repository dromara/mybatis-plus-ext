package com.baomidou.mybatisplus.core.metadata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 注解合并处理类，可以将相同的注解的不同实例，中的属性合并为一个注解实例
 *
 * @author don
 */
@Slf4j
public class AnnotatedElementUtilsPlus extends AnnotatedElementUtils {

    /**
     * 包装类型与基本类型的映射关系
     * 因为枚举中只能存在基本类型，所以，需要把数据包装类型转为基本类型，反射获取枚举的方法
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_MAP = new HashMap<Class<?>, Class<?>>(){{
        put(Boolean.class, boolean.class);
        put(Byte.class, byte.class);
        put(Character.class, char.class);
        put(Short.class, short.class);
        put(Integer.class, int.class);
        put(Long.class, long.class);
        put(Double.class, double.class);
        put(Float.class, float.class);
    }};

    public static <ANNO extends Annotation, IMPL extends ANNO> IMPL findMergedAnnotation(AnnotatedElement element, Class<ANNO> annoClass, Class<IMPL> implClass) {
        final Set<ANNO> allMergedAnnotations = AnnotatedElementUtils.findAllMergedAnnotations(element, annoClass);
        return merge(annoClass, implClass, allMergedAnnotations);
    }

    public static <ANNO extends Annotation, IMPL extends ANNO> IMPL merge(Class<ANNO> annoClass, Class<IMPL> implClass, Collection<ANNO> tableFields) {

        if (tableFields == null || tableFields.isEmpty()) {
            return null;
        }
        try {
            final IMPL defaultImpl = implClass.newInstance();

            final Field[] fields = implClass.getDeclaredFields();
            for (Field field : fields) {
                // 获取默认值
                String fieldName = field.getName();
                final Object defaultVal = implClass.getMethod(fieldName).invoke(defaultImpl);
                tableFields.stream()
                        .map(tf -> {
                            try {
                                // 获取注解的相同字段的值
                                return annoClass.getMethod(fieldName).invoke(tf);
                            } catch (Exception e) {
                                log.warn("合并TableField过程中，获取注解值出错", e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        // 比对，如果与默认值不同，则说明是自定义值
                        .filter(val -> {
                            if (defaultVal.getClass().isArray()) {
                                if (((Object[]) val).length == ((Object[]) defaultVal).length) {
                                    if(((Object[]) val).length != 0) {
                                        return new HashSet<>(Arrays.asList((Object[]) val)).containsAll(Arrays.asList((Object[]) defaultVal));
                                    }
                                }
                                return false;
                            }
                            return !Objects.equals(val, defaultVal);
                        })
                        // 获取第一个，在字段的表现上就是排最上(前)面的那个
                        .findFirst()
                        // 将第一个自定义值设置到默认值对象上
                        .ifPresent(val -> {
                            try {
                                Class<?> paramType = PRIMITIVE_TYPE_MAP.getOrDefault(val.getClass(), val.getClass());
                                implClass.getMethod(fieldName, paramType).invoke(defaultImpl, val);
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.warn("合并TableField过程中，设置默认值出错", e);
                            }
                        });
            }
            return defaultImpl;
        } catch (Exception e) {
            log.warn("合并TableField过程中，获取默认值出错", e);
            return null;
        }
    }
}
