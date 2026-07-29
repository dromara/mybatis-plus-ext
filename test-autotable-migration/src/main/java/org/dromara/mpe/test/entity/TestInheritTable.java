package org.dromara.mpe.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import org.dromara.mpe.autotable.annotation.Column;
import org.dromara.mpe.autotable.annotation.ColumnId;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.autotable.annotation.UniqueIndex;
import org.dromara.autotable.annotation.ColumnComment;
import org.dromara.autotable.annotation.Ignore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 测试继承场景 + @TableField(exist=false) + @Ignore
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
public class TestInheritTable extends TestBaseEntity {

    @ColumnId(value = "id", comment = "id主键", type = "bigint")
    @TableId(type = IdType.AUTO)
    private String id;

    @ColumnComment("姓名")
    private String name;

    @UniqueIndex
    @Column(comment = "邮箱", notNull = true)
    private String email;

    // 测试 @TableField(exist=false) 忽略字段
    @TableField(exist = false)
    private String virtualField;

    // 测试 @Ignore 注解（仅auto-table忽略，MP查询需要配合 @TableField(exist=false)）
    @TableField(exist = false)
    @Ignore
    @Column(comment = "额外信息")
    private String extra;
}
