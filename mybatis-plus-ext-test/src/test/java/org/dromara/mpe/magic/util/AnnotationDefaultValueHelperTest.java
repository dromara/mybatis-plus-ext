package org.dromara.mpe.magic.util;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationDefaultValueHelperTest {

    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {
        String value() default "default";
        int count() default 10;
        String[] tags() default {"a", "b"};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface EmptyAnnotation {
    }

    @Test
    void getDefaultValues_shouldReturnAllDefaults() {
        Map<String, Object> defaults = AnnotationDefaultValueHelper.getDefaultValues(TestAnnotation.class);
        assertEquals("default", defaults.get("value"));
        assertEquals(10, defaults.get("count"));
        assertArrayEquals(new String[]{"a", "b"}, (String[]) defaults.get("tags"));
    }

    @Test
    void getDefaultValues_emptyAnnotation_shouldReturnEmptyMap() {
        Map<String, Object> defaults = AnnotationDefaultValueHelper.getDefaultValues(EmptyAnnotation.class);
        assertTrue(defaults.isEmpty());
    }

    @Test
    void getAnnotationWithDefaultValues_shouldCreateInstanceWithDefaults() {
        TestAnnotation anno = AnnotationDefaultValueHelper.getAnnotationWithDefaultValues(TestAnnotation.class, null);
        assertNotNull(anno);
        assertEquals("default", anno.value());
        assertEquals(10, anno.count());
    }

    @Test
    void getAnnotationWithDefaultValues_shouldAllowOverride() {
        TestAnnotation anno = AnnotationDefaultValueHelper.getAnnotationWithDefaultValues(
                TestAnnotation.class,
                values -> values.put("value", "custom")
        );
        assertEquals("custom", anno.value());
        assertEquals(10, anno.count());
    }

    @Test
    void createAnnotationInstance_shouldReturnWorkingProxy() {
        Map<String, Object> values = new java.util.HashMap<>();
        values.put("value", "test");
        values.put("count", 42);
        values.put("tags", new String[]{"x"});

        TestAnnotation anno = AnnotationDefaultValueHelper.createAnnotationInstance(TestAnnotation.class, values);
        assertEquals("test", anno.value());
        assertEquals(42, anno.count());
        assertArrayEquals(new String[]{"x"}, anno.tags());
    }

    @Test
    void updateAnnotationInstance_shouldOverrideValues() {
        // 先用默认值创建一个实例
        TestAnnotation original = AnnotationDefaultValueHelper.getAnnotationWithDefaultValues(TestAnnotation.class, null);

        Map<String, Object> newValues = new java.util.HashMap<>();
        newValues.put("value", "updated");

        TestAnnotation updated = AnnotationDefaultValueHelper.updateAnnotationInstance(original, TestAnnotation.class, newValues);
        assertEquals("updated", updated.value());
        // count 应该保持原始值
        assertEquals(10, updated.count());
    }
}
