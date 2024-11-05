package org.dromara.mpe.demo.autotable;

import com.tangzc.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.demo.autotable.mysql.MysqlTable;
import org.dromara.mpe.demo.autotable.mysql.MysqlTableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@EnableAutoTableTest(basePackages = "org.dromara.mpe.demo.autotable.mysql")
@SpringBootTest()
public class MysqlTableTest {

    @Autowired
    private MysqlTableRepository mysqlTableRepository;

    @Test
    public void test() {
        MysqlTable entity = new MysqlTable();
        entity.setUsername("mysql");
        mysqlTableRepository.save(entity);

        List<MysqlTable> list = mysqlTableRepository.list();
        assert !list.isEmpty();
    }
}
