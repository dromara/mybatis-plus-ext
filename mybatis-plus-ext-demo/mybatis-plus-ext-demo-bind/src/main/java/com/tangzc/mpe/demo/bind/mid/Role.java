package com.tangzc.mpe.demo.bind.mid;

import lombok.Data;
import lombok.experimental.Accessors;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.BindEntityByMid;
import com.tangzc.mpe.bind.metadata.annotation.BindFieldByMid;
import com.tangzc.mpe.bind.metadata.annotation.MidCondition;

import java.util.List;

/**
 * @author don
 */
@Data
@Accessors(chain = true)
@Table(comment = "角色")
public class Role {

    @Column(comment = "id")
    private String id;
    @Column(comment = "名称")
    private String name;

    @BindEntityByMid(
            selectFields = {MenuDefine.id, MenuDefine.name},
            conditions = @MidCondition(midEntity = RoleMenu.class, selfMidField = RoleMenuDefine.sysRuleId, joinMidField = RoleMenuDefine.sysMenuId)
            // , customCondition = "`name` in ('1','3','5')"
            // , orderBy = @JoinOrderBy(field = MenuDefine.name, isAsc = false)
            // , last = "limit 2"
    )
    private List<Menu> menus;

    @BindFieldByMid(
            entity = Menu.class, field = MenuDefine.name,
            conditions = @MidCondition(midEntity = RoleMenu.class, selfMidField = RoleMenuDefine.sysRuleId, joinMidField = RoleMenuDefine.sysMenuId)
    )
    private List<String> menuNames;
}
