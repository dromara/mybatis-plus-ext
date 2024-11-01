package org.dromara.mpe.demo.dict;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Hello world!
 */
@SpringBootTest
public class TestApp {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Test
    public void test() {

        List<SysUser> sysUsers = sysUserRepository.lambdaQueryPlus().bindList();
        System.out.println(sysUsers.get(0).getSexVal());
    }
}
