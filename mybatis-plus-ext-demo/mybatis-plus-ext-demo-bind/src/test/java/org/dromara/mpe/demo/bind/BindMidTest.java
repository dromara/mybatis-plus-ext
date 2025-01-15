package org.dromara.mpe.demo.bind;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.bind.Binder;
import org.dromara.mpe.demo.bind.mid.Menu;
import org.dromara.mpe.demo.bind.mid.MenuRepository;
import org.dromara.mpe.demo.bind.mid.Role;
import org.dromara.mpe.demo.bind.mid.RoleMenu;
import org.dromara.mpe.demo.bind.mid.RoleMenuRepository;
import org.dromara.mpe.demo.bind.mid.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

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
        menuRepository.saveBatch(Arrays.asList(menu1, menu2), 2);

        Role role = new Role().setId(roleId).setName("角色1");
        roleRepository.save(role);

        roleMenuRepository.saveBatch(Arrays.asList(
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

        assert Arrays.asList("菜单1", "菜单2").containsAll(role.getMenuNames());
        assert role.getMenus().stream().map(Menu::getId).allMatch(Arrays.asList("1", "2")::contains);
    }
}
