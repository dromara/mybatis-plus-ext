package org.dromara.mpe.demo.dict;

import com.tangzc.autotable.springboot.EnableAutoTableTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
@EnableAutoTableTest
@SpringBootTest
public class DictTestApp {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysDictRepository sysDictRepository;

    @BeforeEach
    public void init() {
        List<SysDict> sysDicts = new ArrayList<>();
        sysDicts.add(new SysDict().setDictKey("1").setDictVal("男"));
        sysDicts.add(new SysDict().setDictKey("0").setDictVal("女"));
        sysDictRepository.saveBatch(sysDicts, sysDicts.size());

        List<SysUser> sysUsers = new ArrayList<>();
        sysUsers.add(new SysUser().setName("张三").setSex("1"));
        sysUsers.add(new SysUser().setName("李四").setSex("0"));
        sysUserRepository.saveBatch(sysUsers, sysUsers.size());
    }

    @Test
    public void test() {

        List<SysUser> sysUsers = sysUserRepository.lambdaQueryPlus().bindList();
        for (SysUser sysUser : sysUsers) {
            assert sysUser.getSexVal() != null;
        }
    }
}
