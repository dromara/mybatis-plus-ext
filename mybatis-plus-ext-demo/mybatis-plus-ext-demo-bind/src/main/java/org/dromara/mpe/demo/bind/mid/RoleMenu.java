package org.dromara.mpe.demo.bind.mid;

import com.baomidou.mybatisplus.annotation.TableField;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoDefine;
import lombok.Data;

/**
 * @author don
 */
@Data
@AutoDefine
@Table(comment = "中间表")
public class RoleMenu {

    private String id;
    @TableField("rule_id")
    private String sysRuleId;
    @TableField("menu_id")
    private String sysMenuId;
}
