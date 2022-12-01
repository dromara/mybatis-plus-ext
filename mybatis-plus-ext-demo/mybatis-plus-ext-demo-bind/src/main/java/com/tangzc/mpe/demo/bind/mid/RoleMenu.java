package com.tangzc.mpe.demo.bind.mid;

import com.tangzc.mpe.autotable.annotation.Table;
import lombok.Data;

/**
 * @author don
 */
@Data
@Table(comment = "中间表")
public class RoleMenu {

    private String id;
    private String ruleId;
    private String menuId;
}
