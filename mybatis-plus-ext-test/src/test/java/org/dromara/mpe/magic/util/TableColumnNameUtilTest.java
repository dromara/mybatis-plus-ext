package org.dromara.mpe.magic.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.dromara.mpe.magic.MybatisPlusProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class TableColumnNameUtilTest {

    private boolean origMapUnderscoreToCamelCase;
    private boolean origCapitalMode;
    private String origTablePrefix;

    @BeforeEach
    void setUp() {
        // 保存原始静态值
        origMapUnderscoreToCamelCase = MybatisPlusProperties.mapUnderscoreToCamelCase;
        origCapitalMode = MybatisPlusProperties.capitalMode;
        origTablePrefix = MybatisPlusProperties.tablePrefix;
    }

    @AfterEach
    void tearDown() {
        // 恢复原始静态值
        MybatisPlusProperties.mapUnderscoreToCamelCase = origMapUnderscoreToCamelCase;
        MybatisPlusProperties.capitalMode = origCapitalMode;
        MybatisPlusProperties.tablePrefix = origTablePrefix;
    }

    // --- filterSpecialChar ---

    @Test
    void filterSpecialChar_shouldRemoveBackticks() {
        assertEquals("column_name", TableColumnNameUtil.filterSpecialChar("`column_name`"));
    }

    @Test
    void filterSpecialChar_shouldReturnUnchangedWhenNoBackticks() {
        assertEquals("column_name", TableColumnNameUtil.filterSpecialChar("column_name"));
    }

    // --- getTableName ---

    @TableName("custom_table")
    static class AnnotatedEntity {}

    static class SimpleEntity {}
    static class CamelEntity {}
    static class CapitalEntity {}
    static class PrefixEntity {}

    @Test
    void getTableName_shouldUseTableNameAnnotation() {
        String name = TableColumnNameUtil.getTableName(AnnotatedEntity.class);
        assertEquals("custom_table", name);
    }

    @Test
    void getTableName_shouldConvertCamelToUnderlineWhenEnabled() {
        MybatisPlusProperties.mapUnderscoreToCamelCase = true;
        MybatisPlusProperties.capitalMode = false;
        MybatisPlusProperties.tablePrefix = null;
        String name = TableColumnNameUtil.getTableName(CamelEntity.class);
        assertEquals("camel_entity", name);
    }

    @Test
    void getTableName_shouldApplyCapitalMode() {
        MybatisPlusProperties.mapUnderscoreToCamelCase = false;
        MybatisPlusProperties.capitalMode = true;
        MybatisPlusProperties.tablePrefix = null;
        String name = TableColumnNameUtil.getTableName(CapitalEntity.class);
        assertEquals("CAPITALENTITY", name);
    }

    @Test
    void getTableName_shouldApplyTablePrefix() {
        MybatisPlusProperties.mapUnderscoreToCamelCase = true;
        MybatisPlusProperties.capitalMode = false;
        MybatisPlusProperties.tablePrefix = "t_";
        String name = TableColumnNameUtil.getTableName(PrefixEntity.class);
        assertEquals("t_prefix_entity", name);
    }

    // --- getColumnName ---

    static class ColumnEntity {
        @TableField("custom_col")
        private String annotatedField;

        @TableId("id_col")
        private Long id;

        private String normalField;
    }

    @Test
    void getColumnName_byField_shouldUseTableFieldAnnotation() throws Exception {
        Field field = ColumnEntity.class.getDeclaredField("annotatedField");
        String col = TableColumnNameUtil.getColumnName(field);
        assertEquals("custom_col", col);
    }

    @Test
    void getColumnName_byField_shouldUseTableIdAnnotation() throws Exception {
        Field field = ColumnEntity.class.getDeclaredField("id");
        String col = TableColumnNameUtil.getColumnName(field);
        assertEquals("id_col", col);
    }

    @Test
    void getColumnName_byField_shouldFallbackToSmartConvert() throws Exception {
        MybatisPlusProperties.mapUnderscoreToCamelCase = true;
        MybatisPlusProperties.capitalMode = false;
        Field field = ColumnEntity.class.getDeclaredField("normalField");
        String col = TableColumnNameUtil.getColumnName(field);
        assertEquals("normal_field", col);
    }

    @Test
    void getColumnName_byClassAndFieldName_shouldWork() {
        MybatisPlusProperties.mapUnderscoreToCamelCase = true;
        MybatisPlusProperties.capitalMode = false;
        String col = TableColumnNameUtil.getColumnName(ColumnEntity.class, "annotatedField");
        assertEquals("custom_col", col);
    }
}
