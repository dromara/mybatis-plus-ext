package com.tangzc.mpe.demo.autotable;

import com.tangzc.autotable.springboot.EnableAutoTableTest;
import com.tangzc.mpe.demo.autotable.pgsql.PgsqlTable;
import com.tangzc.mpe.demo.autotable.pgsql.PgsqlTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@EnableAutoTableTest(basePackages = "com.tangzc.mpe.demo.autotable.pgsql")
@SpringBootTest()
public class PgsqlTableTest {

    @Autowired
    private PgsqlTableRepository pgsqlTableRepository;

    // @Test
    public void test() {
        PgsqlTable entity = new PgsqlTable();
        entity.setPhone("1234567890");
        entity.setMoney(BigDecimal.ONE);
        pgsqlTableRepository.save(entity);

        List<PgsqlTable> list = this.pgsqlTableRepository.list();
        assert !list.isEmpty();
    }
}
