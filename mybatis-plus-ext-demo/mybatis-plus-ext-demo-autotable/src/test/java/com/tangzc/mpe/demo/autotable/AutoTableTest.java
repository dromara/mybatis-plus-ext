package com.tangzc.mpe.demo.autotable;

import com.tangzc.mpe.demo.autotable.mysql.MysqlTable;
import com.tangzc.mpe.demo.autotable.mysql.MysqlTableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
public class AutoTableTest {

    @Autowired
    private MysqlTableRepository mysqlTableRepository;

    @Test
    public void insert() {
        MysqlTable entity = new MysqlTable();
        entity.setUsername("张三");
        mysqlTableRepository.save(entity);
    }
}
