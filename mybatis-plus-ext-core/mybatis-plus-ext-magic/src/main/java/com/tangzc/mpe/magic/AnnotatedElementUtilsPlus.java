package com.tangzc.mpe.magic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 注解合并处理类，可以将相同的注解的不同实例，中的属性合并为一个注解实例
 *
 * @author don
 */
@Slf4j
public class AnnotatedElementUtilsPlus extends AnnotatedElementUtils {

    public static <ANNO extends Annotation> ANNO findDeepMergedAnnotation(AnnotatedElement element, Class<ANNO> annoClass) {
        final Set<ANNO> allMergedAnnotations = AnnotatedElementUtils.findAllMergedAnnotations(element, annoClass);
        return merge(annoClass, allMergedAnnotations);
    }

    public static <ANNO extends Annotation> ANNO merge(Class<ANNO> annoClass, Set<ANNO> allAnnotations) {

        if (allAnnotations == null || allAnnotations.isEmpty()) {
            return null;
        }
        try {
            ANNO defaultAnno = AnnotationUtils.synthesizeAnnotation(annoClass);
            final Method[] annoMethods = annoClass.getDeclaredMethods();
            for (Method annoMethod : annoMethods) {
                // 获取默认值
                String annoFieldName = annoMethod.getName();
                Object defaultVal = null;
                try {
                    defaultVal = AnnotationUtils.getAnnotationAttributes(defaultAnno).get(annoFieldName);
                } catch (Exception ignore) {
                    log.debug("{}的{}无默认值", annoClass, annoFieldName);
                }
                // 从当前所有注解中，寻找fieldName属性第一个不同于默认值且不为null的值
                // 获取第一个符合的值，在字段的表现上就是：按照从上(前)到下(后)的注解找
                Object newVal = null;
                for (ANNO annotation : allAnnotations) {
                    try {
                        // 获取注解的相同字段的值
                        Object annoVal = annoClass.getMethod(annoFieldName).invoke(annotation);
                        // 比对，如果与默认值不同，则说明是自定义值
                        if (compareValIsDiff(annoVal, defaultVal)) {
                            newVal = annoVal;
                            break;
                        }
                    } catch (Exception e) {
                        log.warn("合并TableField过程中，获取注解值出错", e);
                    }
                }
                // 将第一个自定义值设置到默认值对象上
                if (newVal != null) {
                    setAnnoVal(defaultAnno, annoFieldName, newVal);
                }
            }
            return defaultAnno;
        } catch (Exception e) {
            log.warn("合并TableField过程中，获取默认值出错", e);
            return null;
        }
    }

    private static boolean compareValIsDiff(Object val, Object defVal) {

        if (val == null) {
            return false;
        }

        if (defVal == null) {
            return true;
        }

        if (defVal.getClass().isArray()) {
            // 长度相等的情况下，在比对内容
            List<Object> list = Arrays.asList(val);
            List<Object> defList = Arrays.asList(defVal);
            if (list.size() > 0 && list.size() == defList.size()) {
                // 考虑到数组中，可能值的顺序不同，所以使用containsAll方法。
                // 因为大小相同，如果是一个集合全包含另一个集合，则说明两个集合内容完全一致
                return !list.containsAll(defList);
            }
            return list.size() > 0;
        }
        return !Objects.equals(val, defVal);
    }

    /**
     * 获取类注解属性
     */
    private static void setAnnoVal(Annotation annotation, String fieldName, Object newVal) {
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
}
