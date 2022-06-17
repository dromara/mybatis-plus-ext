package com.tangzc.mpe.demo.bind.mid;

import com.tangzc.mpe.actable.annotation.Column;
import com.tangzc.mpe.actable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.BindEntityByMid;
import com.tangzc.mpe.bind.metadata.annotation.JoinOrderBy;
import com.tangzc.mpe.bind.metadata.annotation.MidCondition;
import lombok.Data;

import java.util.List;

/**
 * @author don
 */
@Data
@Table(comment = "角色")
public class Role {

    @Column(comment = "id")
    private String id;
    @Column(comment = "名称")
    private String name;

    @BindEntityByMid(
            conditions = @MidCondition(midEntity = RoleMenu.class, selfMidField = "ruleId", joinMidField = "menuId"),
            customCondition = "`name` in ('1','3','5')",
            orderBy = @JoinOrderBy(field = "name", isAsc = false),
            last = "limit 2"
    )
    private List<Menu> menus;
}
