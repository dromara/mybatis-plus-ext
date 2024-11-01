package org.dromara.mpe.demo.bind.mid;

import org.dromara.mpe.bind.Binder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("mid-bind")
public class TestController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("list")
    public List<Role> list() {
        roleRepository.lambdaQuery().eq(Role::getId, "1").list();
        List<Role> list = roleRepository.list();
        Binder.bind(list);
        return list;
    }
}
