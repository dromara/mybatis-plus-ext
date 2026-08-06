package org.dromara.mpe.autotable;

import com.baomidou.mybatisplus.annotation.IdType;
import org.dromara.autotable.adapter.mybatisplus.MybatisPlusAdapterConfig;
import org.dromara.mpe.autotable.annotation.Column;
import org.dromara.mpe.autotable.annotation.ColumnId;
import org.dromara.mpe.autotable.annotation.Table;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ExtMetadataAdapter} 单元测试。
 * <p>
 * 重点回归：ext 注解（@Table/@Column/@ColumnId）显式指定的表名/列名必须原样使用，
 * 不能再经过 smartConvert（驼峰转下划线/大写），否则会破坏 Oracle 等全大写命名，
 * 并导致 DDL 建表名与运行时 SQL 使用名不一致。
 *
 * @author don
 */
class ExtMetadataAdapterTest {

    /**
     * 构造一个空的 IgnoreExt Provider（本测试仅覆盖表名/列名解析，不触发 IgnoreExt 钩子）。
     */
    private static ObjectProvider<IgnoreExt> emptyIgnoreProvider() {
        return new ObjectProvider<IgnoreExt>() {
            @Override
            public IgnoreExt getObject(Object... args) {
                return null;
            }

            @Override
            public IgnoreExt getObject() {
                return null;
            }

            @Override
            public IgnoreExt getIfAvailable() {
                return null;
            }

            @Override
            public IgnoreExt getIfUnique() {
                return null;
            }

            @Override
            public Stream<IgnoreExt> stream() {
                return Stream.empty();
            }

            @Override
            public Stream<IgnoreExt> orderedStream() {
                return Stream.empty();
            }
        };
    }

    private ExtMetadataAdapter newAdapter(boolean mapUnderscoreToCamelCase, boolean capitalMode, String tablePrefix) {
        MybatisPlusAdapterConfig config = new MybatisPlusAdapterConfig();
        config.setMapUnderscoreToCamelCase(mapUnderscoreToCamelCase);
        config.setCapitalMode(capitalMode);
        config.setTablePrefix(tablePrefix);
        return new ExtMetadataAdapter(config, emptyIgnoreProvider());
    }

    private Field field(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    // ===== 测试实体 =====

    /** 模拟 Oracle 全大写命名习惯：显式表名带下划线、列名全大写 */
    @Table(value = "LY_RZ_ZY", comment = "资源")
    static class ExplicitUpperEntity {
        @ColumnId(value = "SSBH", comment = "编号", length = 64)
        private String sourceId;

        @Column(value = "ZYMC", comment = "名称", length = 100)
        private String name;

        /** 无显式列名 → 兜底走字段名驼峰转下划线 */
        @Column(comment = "附件路径", length = 500)
        private String attachmentPath;

        /** 无任何 ext 注解 → 兜底走字段名驼峰转下划线 */
        private String plainField;
    }

    /** 无 @Table → 兜底走类名转换 */
    static class NoTableEntity {
    }

    /** 显式值带反引号 → 应被 filterSpecialChar 去除 */
    @Table(value = "`weird_table`")
    static class BacktickEntity {
        @Column(value = "`weird_col`")
        private String x;
    }

    /** keepGlobalPrefix 默认 false → 全局前缀不生效 */
    @Table(value = "MY_TABLE")
    static class NoKeepPrefixEntity {
    }

    /** keepGlobalPrefix = true → 全局前缀生效 */
    @Table(value = "MY_TABLE", keepGlobalPrefix = true)
    static class KeepPrefixEntity {
    }

    // ===== getTableName =====

    @Test
    void getTableName_explicitUppercaseWithUnderscore_keptVerbatim() {
        // mapUnderscoreToCamelCase=true（MP 默认），显式表名必须原样，不能变成 l_y__r_z__z_y
        ExtMetadataAdapter adapter = newAdapter(true, false, "");
        String name = adapter.getTableName(ExplicitUpperEntity.class);
        assertEquals("LY_RZ_ZY", name, "显式表名应原样使用");
        assertNotEquals("l_y__r_z__z_y", name, "显式表名不应被驼峰转下划线破坏");
    }

    @Test
    void getTableName_explicitValue_notAffectedByCapitalMode() {
        // 即便 capitalMode=true，也不应对显式值做额外处理（保持原样）
        ExtMetadataAdapter adapter = newAdapter(true, true, "");
        assertEquals("LY_RZ_ZY", adapter.getTableName(ExplicitUpperEntity.class));
    }

    @Test
    void getTableName_noTableAnnotation_fallbackConvertsClassName() {
        // 无 @Table，兜底：类名 NoTableEntity → no_table_entity
        ExtMetadataAdapter adapter = newAdapter(true, false, "");
        assertEquals("no_table_entity", adapter.getTableName(NoTableEntity.class));
    }

    @Test
    void getTableName_backticks_filtered() {
        ExtMetadataAdapter adapter = newAdapter(true, false, "");
        assertEquals("weird_table", adapter.getTableName(BacktickEntity.class));
    }

    @Test
    void getTableName_keepGlobalPrefixFalse_prefixNotAdded() {
        ExtMetadataAdapter adapter = newAdapter(true, false, "t_");
        assertEquals("MY_TABLE", adapter.getTableName(NoKeepPrefixEntity.class), "keepGlobalPrefix=false 时不应追加前缀");
    }

    @Test
    void getTableName_keepGlobalPrefixTrue_prefixAdded() {
        ExtMetadataAdapter adapter = newAdapter(true, false, "t_");
        assertEquals("t_MY_TABLE", adapter.getTableName(KeepPrefixEntity.class), "keepGlobalPrefix=true 时应追加前缀");
    }

    // ===== getColumnName =====

    @Test
    void getColumnName_columnIdExplicitUppercase_keptVerbatim() {
        ExtMetadataAdapter adapter = newAdapter(true, false, "");
        String col = adapter.getColumnName(ExplicitUpperEntity.class, field(ExplicitUpperEntity.class, "sourceId"));
        assertEquals("SSBH", col, "@ColumnId 显式列名应原样使用");
        assertNotEquals("s_s_b_h", col, "@ColumnId 显式列名不应被驼峰转下划线破坏");
    }

    @Test
    void getColumnName_columnExplicitUppercase_keptVerbatim() {
        ExtMetadataAdapter adapter = newAdapter(true, false, "");
        String col = adapter.getColumnName(ExplicitUpperEntity.class, field(ExplicitUpperEntity.class, "name"));
        assertEquals("ZYMC", col, "@Column 显式列名应原样使用");
    }

    @Test
    void getColumnName_columnWithoutValue_fallbackConvertsFieldName() {
        // @Column 无 value → 兜底：attachmentPath → attachment_path
        ExtMetadataAdapter adapter = newAdapter(true, false, "");
        String col = adapter.getColumnName(ExplicitUpperEntity.class, field(ExplicitUpperEntity.class, "attachmentPath"));
        assertEquals("attachment_path", col, "无显式值的 @Column 应兜底做驼峰转下划线");
    }

    @Test
    void getColumnName_noAnnotation_fallbackConvertsFieldName() {
        ExtMetadataAdapter adapter = newAdapter(true, false, "");
        String col = adapter.getColumnName(ExplicitUpperEntity.class, field(ExplicitUpperEntity.class, "plainField"));
        assertEquals("plain_field", col, "无注解字段应兜底做驼峰转下划线");
    }

    @Test
    void getColumnName_backticks_filtered() {
        ExtMetadataAdapter adapter = newAdapter(true, false, "");
        String col = adapter.getColumnName(BacktickEntity.class, field(BacktickEntity.class, "x"));
        assertEquals("weird_col", col);
    }

    // ===== isPrimary / isAutoIncrement（附带回归，确保 @ColumnId 主键语义未受影响）=====

    @Table(value = "PK_TABLE")
    static class PkEntity {
        @ColumnId(value = "ID", mode = IdType.AUTO)
        private Long id;

        @Column(value = "NAME")
        private String name;
    }

    @Test
    void isPrimary_columnIdField_returnsTrue() {
        ExtMetadataAdapter adapter = newAdapter(true, false, "");
        assertTrue(adapter.isPrimary(field(PkEntity.class, "id"), PkEntity.class));
        assertFalse(adapter.isPrimary(field(PkEntity.class, "name"), PkEntity.class));
    }

    @Test
    void isAutoIncrement_columnIdAutoMode_returnsTrue() {
        ExtMetadataAdapter adapter = newAdapter(true, false, "");
        assertTrue(adapter.isAutoIncrement(field(PkEntity.class, "id"), PkEntity.class));
    }
}
