package org.dromara.mpe.test;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.test.entity.TestInheritTable;
import org.dromara.mpe.test.entity.TestInheritTableMapper;
import org.dromara.mpe.test.entity.TestTable;
import org.dromara.mpe.test.entity.TestTableMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoTableTest(basePackages = "org.dromara.mpe.test.entity")
@SpringBootTest()
public class AutoTableMigrationTest {

    @Autowired
    private TestTableMapper testTableMapper;

    @Autowired
    private TestInheritTableMapper testInheritTableMapper;

    @Autowired
    private DataSource dataSource;

    // ===== 1. 测试自动建表 =====

    @Test
    public void testTableCreated() throws Exception {
        // 验证 my_test_table 表已自动创建（带表前缀 my_）
        List<String> tables = getTableNames();
        System.out.println("已创建的表: " + tables);
        assertTrue(tables.stream().anyMatch(t -> t.equalsIgnoreCase("my_test_table")),
                "表 my_test_table 应该已被自动创建");
    }

    @Test
    public void testInheritTableCreated() throws Exception {
        // 验证继承场景的表也被创建
        List<String> tables = getTableNames();
        assertTrue(tables.stream().anyMatch(t -> t.equalsIgnoreCase("my_test_inherit_table")),
                "表 my_test_inherit_table 应该已被自动创建");
    }

    // ===== 2. 测试列定义 =====

    @Test
    public void testColumnDefinitions() throws Exception {
        List<String> columns = getColumnNames("my_test_table");
        System.out.println("my_test_table 的列: " + columns);

        // 验证核心列存在
        assertTrue(containsIgnoreCase(columns, "id"), "id 列应存在");
        assertTrue(containsIgnoreCase(columns, "username"), "username 列应存在");
        assertTrue(containsIgnoreCase(columns, "age"), "age 列应存在");
        assertTrue(containsIgnoreCase(columns, "phone"), "phone 列应存在");
        assertTrue(containsIgnoreCase(columns, "money"), "money 列应存在");
        assertTrue(containsIgnoreCase(columns, "active"), "active 列应存在");
        assertTrue(containsIgnoreCase(columns, "description"), "description 列应存在");
        assertTrue(containsIgnoreCase(columns, "register_time"), "register_time 列应存在");
    }

    @Test
    public void testInheritTableColumns() throws Exception {
        List<String> columns = getColumnNames("my_test_inherit_table");
        System.out.println("my_test_inherit_table 的列: " + columns);

        // 验证父类字段被继承
        assertTrue(containsIgnoreCase(columns, "base_column1"), "base_column1（继承字段）应存在");
        assertTrue(containsIgnoreCase(columns, "base_column2"), "base_column2（继承字段）应存在");
        // 验证自身字段
        assertTrue(containsIgnoreCase(columns, "id"), "id 列应存在");
        assertTrue(containsIgnoreCase(columns, "name"), "name 列应存在");
        assertTrue(containsIgnoreCase(columns, "email"), "email 列应存在");
        // 验证 @TableField(exist=false) 的字段不被建表
        assertFalse(containsIgnoreCase(columns, "virtual_field"), "virtual_field（@TableField(exist=false)）不应存在");
        // 验证 @Ignore 的字段不被建表
        assertFalse(containsIgnoreCase(columns, "extra"), "extra（@Ignore）不应存在");
    }

    // ===== 3. 测试 CRUD =====

    @Test
    public void testCrudOperations() {
        // 插入
        TestTable entity = new TestTable();
        entity.setUsername("testuser");
        entity.setPhone("1234567890");
        entity.setAge(25);
        entity.setMoney(new BigDecimal("100.50"));
        entity.setActive(true);
        entity.setDescription("测试简介");
        entity.setRegisterTime("2024-01-01 00:00:00");

        int rows = testTableMapper.insert(entity);
        assertEquals(1, rows, "插入应成功");
        assertNotNull(entity.getId(), "插入后 id 应被自动填充");

        // 查询
        TestTable found = testTableMapper.selectById(entity.getId());
        assertNotNull(found, "应能查到插入的数据");
        assertEquals("testuser", found.getUsername());
        assertEquals("1234567890", found.getPhone());
        assertEquals(25, found.getAge());

        // 更新
        found.setUsername("updated_user");
        int updateRows = testTableMapper.updateById(found);
        assertEquals(1, updateRows, "更新应成功");

        TestTable updated = testTableMapper.selectById(entity.getId());
        assertEquals("updated_user", updated.getUsername());

        // 列表查询
        List<TestTable> list = testTableMapper.selectList(null);
        assertFalse(list.isEmpty(), "列表查询不应为空");
    }

    @Test
    public void testInheritTableCrud() {
        TestInheritTable entity = new TestInheritTable();
        entity.setName("张三");
        entity.setEmail("test@example.com");
        entity.setBaseColumn1("base1");
        entity.setBaseColumn2("base2");

        int rows = testInheritTableMapper.insert(entity);
        assertEquals(1, rows, "继承表插入应成功");

        TestInheritTable found = testInheritTableMapper.selectById(entity.getId());
        assertNotNull(found);
        assertEquals("张三", found.getName());
        assertEquals("test@example.com", found.getEmail());
        assertEquals("base1", found.getBaseColumn1());
        assertEquals("base2", found.getBaseColumn2());
    }

    // ===== 辅助方法 =====

    private List<String> getTableNames() throws Exception {
        List<String> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
        }
        return tables;
    }

    private List<String> getColumnNames(String tableName) throws Exception {
        List<String> columns = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, tableName, "%")) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME"));
                }
            }
            // 如果按原名找不到，尝试大写
            if (columns.isEmpty()) {
                try (ResultSet rs = meta.getColumns(null, null, tableName.toUpperCase(), "%")) {
                    while (rs.next()) {
                        columns.add(rs.getString("COLUMN_NAME"));
                    }
                }
            }
        }
        return columns;
    }

    private boolean containsIgnoreCase(List<String> list, String item) {
        return list.stream().anyMatch(s -> s.equalsIgnoreCase(item));
    }
}
