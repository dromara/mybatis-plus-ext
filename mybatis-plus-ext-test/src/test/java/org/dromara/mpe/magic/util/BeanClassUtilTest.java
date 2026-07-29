package org.dromara.mpe.magic.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BeanClassUtilTest {

    // --- 测试用的辅助类 ---

    static class Parent {
        private String parentField;
        protected String protectedField;
        public static final String STATIC_FIELD = "static";
    }

    static class Child extends Parent {
        private String childField;
        private List<String> stringList;
    }

    static class Simple {
        private String name;
        private int age;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    // --- getField ---

    @Test
    void getField_shouldFindFieldInCurrentClass() {
        Field field = BeanClassUtil.getField(Simple.class, "name");
        assertNotNull(field);
        assertEquals("name", field.getName());
    }

    @Test
    void getField_shouldFindFieldInParentClass() {
        Field field = BeanClassUtil.getField(Child.class, "parentField");
        assertNotNull(field);
        assertEquals("parentField", field.getName());
    }

    @Test
    void getField_shouldThrowWhenFieldNotFound() {
        assertThrows(RuntimeException.class, () -> BeanClassUtil.getField(Simple.class, "nonExistent"));
    }

    // --- getFieldRealClass ---

    @Test
    void getFieldRealClass_shouldReturnFieldType() throws Exception {
        Field field = Simple.class.getDeclaredField("name");
        assertEquals(String.class, BeanClassUtil.getFieldRealClass(field));
    }

    @Test
    void getFieldRealClass_shouldReturnCollectionGenericType() throws Exception {
        Field field = Child.class.getDeclaredField("stringList");
        assertEquals(String.class, BeanClassUtil.getFieldRealClass(field));
    }

    @Test
    void getFieldRealClass_shouldReturnFieldTypeForNonCollection() throws Exception {
        Field field = Simple.class.getDeclaredField("age");
        assertEquals(int.class, BeanClassUtil.getFieldRealClass(field));
    }

    // --- getWriteMethod / getReadMethod ---

    @Test
    void getWriteMethod_shouldReturnSetter() {
        Method method = BeanClassUtil.getWriteMethod(Simple.class, "name");
        assertNotNull(method);
        assertEquals("setName", method.getName());
    }

    @Test
    void getWriteMethod_shouldThrowWhenNoSetter() {
        assertThrows(RuntimeException.class, () -> BeanClassUtil.getWriteMethod(Parent.class, "parentField"));
    }

    @Test
    void getReadMethod_shouldReturnGetter() {
        Method method = BeanClassUtil.getReadMethod(Simple.class, "name");
        assertNotNull(method);
        assertEquals("getName", method.getName());
    }

    @Test
    void getReadMethod_shouldThrowWhenNoGetter() {
        assertThrows(RuntimeException.class, () -> BeanClassUtil.getReadMethod(Parent.class, "parentField"));
    }

    @Test
    void getWriteMethod_byField_shouldReturnSetter() throws Exception {
        Field field = Simple.class.getDeclaredField("name");
        Method method = BeanClassUtil.getWriteMethod(Simple.class, field);
        assertNotNull(method);
        assertEquals("setName", method.getName());
    }

    @Test
    void getReadMethod_byField_shouldReturnGetter() throws Exception {
        Field field = Simple.class.getDeclaredField("name");
        Method method = BeanClassUtil.getReadMethod(Simple.class, field);
        assertNotNull(method);
        assertEquals("getName", method.getName());
    }

    // --- getAllDeclaredFields ---

    @Test
    void getAllDeclaredFields_shouldIncludeParentFields() {
        List<Field> fields = BeanClassUtil.getAllDeclaredFields(Child.class);
        assertTrue(fields.size() >= 4); // childField, stringList, parentField, protectedField, STATIC_FIELD
    }

    @Test
    void getAllDeclaredFieldsExcludeStatic_shouldExcludeStaticFields() {
        List<Field> fields = BeanClassUtil.getAllDeclaredFieldsExcludeStatic(Child.class);
        assertTrue(fields.stream().noneMatch(f -> f.getName().equals("STATIC_FIELD")));
    }

    @Test
    void getAllDeclaredFieldsExcludeStatic_shouldKeepNonStaticParentFields() {
        List<Field> fields = BeanClassUtil.getAllDeclaredFieldsExcludeStatic(Child.class);
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("parentField")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("childField")));
    }
}
