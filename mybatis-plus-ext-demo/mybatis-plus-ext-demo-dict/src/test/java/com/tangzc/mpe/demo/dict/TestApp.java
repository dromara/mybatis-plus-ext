package com.tangzc.mpe.demo.dict;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Hello world!
 *
 */
@SpringBootTest
public class TestApp {

    @Resource
    private SysUserRepository sysUserRepository;

    @Test
    public void test() {

        List<SysUser> sysUsers = sysUserRepository.lambdaQueryPlus().bindList();
        System.out.println(sysUsers.get(0).getSexVal());
    }
}
