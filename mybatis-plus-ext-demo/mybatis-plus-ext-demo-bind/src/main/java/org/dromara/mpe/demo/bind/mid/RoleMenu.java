package org.dromara.mpe.demo.bind.mid;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.experimental.Accessors;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoDefine;
import lombok.Data;

/**
 * @author don
 */
@Data
@Accessors(chain = true)
@Table(comment = "中间表")
public class RoleMenu {

    private String id;
    @TableField("rule_id")
    private String sysRuleId;
    @TableField("menu_id")
    private String sysMenuId;
}
