package com.tangzc.mpe.demo.bind;

import com.tangzc.autotable.springboot.EnableAutoTableTest;
import com.tangzc.mpe.bind.Binder;
import com.tangzc.mpe.demo.bind.mid.Menu;
import com.tangzc.mpe.demo.bind.mid.MenuRepository;
import com.tangzc.mpe.demo.bind.mid.Role;
import com.tangzc.mpe.demo.bind.mid.RoleMenu;
import com.tangzc.mpe.demo.bind.mid.RoleMenuRepository;
import com.tangzc.mpe.demo.bind.mid.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

@EnableAutoTableTest
@SpringBootTest
public class BindMidTest {

    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleMenuRepository roleMenuRepository;
    String roleId = "1";

    @BeforeEach
    public void init() {
        Menu menu1 = new Menu().setId("1").setName("菜单1");
        Menu menu2 = new Menu().setId("2").setName("菜单2");
        menuRepository.saveBatch(List.of(menu1, menu2), 2);

        Role role = new Role().setId(roleId).setName("角色1");
        roleRepository.save(role);

        roleMenuRepository.saveBatch(List.of(
                new RoleMenu().setId("1").setSysMenuId(menu1.getId()).setSysRuleId(roleId),
                new RoleMenu().setId("2").setSysMenuId(menu2.getId()).setSysRuleId(roleId)
        ), 2);
    }

    /**
     * 普通的绑定方式
     */
    @Test
    public void midBind() {

        Role role = roleRepository.getById(roleId);
        Binder.bind(role);

        assert Set.of("菜单1", "菜单2").containsAll(role.getMenuNames());
        assert role.getMenus().stream().map(Menu::getId).allMatch(Set.of("1", "2")::contains);
    }
}
