package org.dromara.mpe.test.integration;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.test.dict.SysDict;
import org.dromara.mpe.test.dict.SysDictRepository;
import org.dromara.mpe.test.dict.SysUser;
import org.dromara.mpe.test.dict.SysUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoTableTest
@SpringBootTest
class DictTest {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysDictRepository sysDictRepository;

    @BeforeEach
    void init() {
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
    void testDictBind() {
        List<SysUser> sysUsers = sysUserRepository.lambdaQueryPlus().bindList();
        assertFalse(sysUsers.isEmpty());
        sysUsers.forEach(sysUser -> {
            assertNotNull(sysUser.getSexVal(), "字典值应被自动填充");
        });
    }
}
