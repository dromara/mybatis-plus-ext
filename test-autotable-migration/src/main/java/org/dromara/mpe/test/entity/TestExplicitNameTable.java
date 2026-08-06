package org.dromara.mpe.test.entity;

import lombok.Data;
import org.dromara.mpe.autotable.annotation.Column;
import org.dromara.mpe.autotable.annotation.ColumnId;
import org.dromara.mpe.autotable.annotation.Table;

/**
 * 测试实体：显式指定全大写表名/列名（模拟 Oracle 命名习惯）
 * <p>
 * 回归场景：注解显式指定的名字必须原样使用，不能再做驼峰转下划线转换。
 * 例如 "SSBH" 不能被转成 "s_s_b_h"，"LY_RZ_ZY" 不能被转成 "l_y__r_z__z_y"。
 */
@Data
@Table(value = "LY_RZ_ZY", comment = "资源", dsName = "my-sqlite")
public class TestExplicitNameTable {

    @ColumnId(value = "SSBH", comment = "文档库所属编号", length = 64)
    private String sourceId;

    @Column(value = "ZYMC", comment = "资源名称", length = 100)
    private String name;

    /**
     * 无显式列名，兜底走字段名驼峰转下划线 → attachment_path
     */
    @Column(comment = "附件路径", length = 500)
    private String attachmentPath;
}
