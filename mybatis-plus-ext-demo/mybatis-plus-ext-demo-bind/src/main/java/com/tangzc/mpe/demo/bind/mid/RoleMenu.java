package com.tangzc.mpe.demo.bind.mid;

import com.baomidou.mybatisplus.annotation.TableField;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.Data;

/**
 * @author don
 */
@Data
@Table(comment = "中间表")
public class RoleMenu {

    private String id;
    @TableField("rule_id")
    private String sysRuleId;
    @TableField("menu_id")
    private String sysMenuId;
}
