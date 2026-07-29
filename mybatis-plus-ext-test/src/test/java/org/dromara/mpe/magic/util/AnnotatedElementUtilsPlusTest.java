package org.dromara.mpe.magic.util;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AnnotatedElementUtilsPlusTest {

    @Retention(RetentionPolicy.RUNTIME)
    @interface MyAnno {
        String value() default "";
        int count() default 0;
    }

    @MyAnno(value = "first", count = 5)
    static class AnnotatedClass {}

    static class NoAnnoClass {}

    @Test
    void merge_nullSet_shouldReturnNull() {
        assertNull(AnnotatedElementUtilsPlus.merge(MyAnno.class, null));
    }

    @Test
    void merge_emptySet_shouldReturnNull() {
        assertNull(AnnotatedElementUtilsPlus.merge(MyAnno.class, new HashSet<>()));
    }

    @Test
    void getDeepMergedAnnotation_shouldReturnAnnotation() {
        MyAnno anno = AnnotatedElementUtilsPlus.getDeepMergedAnnotation(AnnotatedClass.class, MyAnno.class);
        assertNotNull(anno);
        assertEquals("first", anno.value());
        assertEquals(5, anno.count());
    }

    @Test
    void getDeepMergedAnnotation_noAnno_shouldReturnNull() {
        MyAnno anno = AnnotatedElementUtilsPlus.getDeepMergedAnnotation(NoAnnoClass.class, MyAnno.class);
        assertNull(anno);
    }

    @Test
    void findDeepMergedAnnotation_shouldFindInheritedAnnotation() {
        MyAnno anno = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(AnnotatedClass.class, MyAnno.class);
        assertNotNull(anno);
        assertEquals("first", anno.value());
    }

    @Test
    void findDeepMergedAnnotation_noAnno_shouldReturnNull() {
        MyAnno anno = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(NoAnnoClass.class, MyAnno.class);
        assertNull(anno);
    }

    // 测试合并多个注解的场景
    @Test
    void merge_multipleAnnotations_shouldMergeValues() {
        MyAnno anno1 = AnnotationDefaultValueHelper.getAnnotationWithDefaultValues(
                MyAnno.class, values -> { values.put("value", "a"); values.put("count", 1); });
        MyAnno anno2 = AnnotationDefaultValueHelper.getAnnotationWithDefaultValues(
                MyAnno.class, values -> { values.put("value", "b"); values.put("count", 2); });

        Set<MyAnno> set = new HashSet<>();
        set.add(anno1);
        set.add(anno2);

        MyAnno merged = AnnotatedElementUtilsPlus.merge(MyAnno.class, set);
        assertNotNull(merged);
        // 合并后应取到非默认值
        assertTrue("a".equals(merged.value()) || "b".equals(merged.value()));
        assertTrue(merged.count() == 1 || merged.count() == 2);
    }
}
