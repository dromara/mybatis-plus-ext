package com.tangzc.mpe.magic.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
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

    public static <ANNO extends Annotation> ANNO getDeepMergedAnnotation(AnnotatedElement element, Class<ANNO> annoClass) {
        final Set<ANNO> allMergedAnnotations = AnnotatedElementUtils.getAllMergedAnnotations(element, annoClass);
        return merge(annoClass, allMergedAnnotations);
    }

    public static <ANNO extends Annotation> ANNO findDeepMergedAnnotation(AnnotatedElement element, Class<ANNO> annoClass) {
        final Set<ANNO> allMergedAnnotations = AnnotatedElementUtils.findAllMergedAnnotations(element, annoClass);
        return merge(annoClass, allMergedAnnotations);
    }

    public static <ANNO extends Annotation> ANNO merge(Class<ANNO> annoClass, Set<ANNO> allAnnotations) {

        if (allAnnotations == null || allAnnotations.isEmpty()) {
            return null;
        }
        try {
            Map<String, Object> annoDefaultAttributes = AnnotationDefaultValueHelper.getDefaultValues(annoClass);
            for (Map.Entry<String, Object> attribute : annoDefaultAttributes.entrySet()) {
                // 获取默认值
                String annoFieldName = attribute.getKey();
                Object defaultVal = attribute.getValue();

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
                            // 找到不同就退出了，此时体现就是，要么该注解是最贴近原始注解的注解，要么该注解是在最上(前)面的那个注解
                            break;
                        }
                    } catch (Exception e) {
                        log.warn("合并TableField过程中，获取注解值出错", e);
                    }
                }
                // 将第一个自定义值设置到默认值对象上
                if (newVal != null) {
                    annoDefaultAttributes.put(annoFieldName, newVal);
                }
            }
            return AnnotationDefaultValueHelper.createAnnotationInstance(annoClass, annoDefaultAttributes);
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
            if (!list.isEmpty() && list.size() == defList.size()) {
                // 考虑到数组中，可能值的顺序不同，所以使用containsAll方法。
                // 因为大小相同，如果是一个集合全包含另一个集合，则说明两个集合内容完全一致
                return !list.containsAll(defList);
            }
            return !list.isEmpty();
        }
        return !Objects.equals(val, defVal);
    }
}
