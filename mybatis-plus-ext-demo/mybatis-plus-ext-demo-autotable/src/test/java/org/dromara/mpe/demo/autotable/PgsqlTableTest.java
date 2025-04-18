package org.dromara.mpe.demo.autotable;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.demo.autotable.pgsql.PgsqlTable;
import org.dromara.mpe.demo.autotable.pgsql.PgsqlTableRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@EnableAutoTableTest(basePackages = "org.dromara.mpe.demo.autotable.pgsql")
@SpringBootTest()
@Disabled
public class PgsqlTableTest {

    @Autowired
    private PgsqlTableRepository pgsqlTableRepository;

    @Test
    public void test() {
        PgsqlTable entity = new PgsqlTable();
        entity.setPhone("1234567890");
        entity.setMoney(BigDecimal.ONE);
        pgsqlTableRepository.save(entity);

        List<PgsqlTable> list = this.pgsqlTableRepository.list();
        assert !list.isEmpty();
    }
}
