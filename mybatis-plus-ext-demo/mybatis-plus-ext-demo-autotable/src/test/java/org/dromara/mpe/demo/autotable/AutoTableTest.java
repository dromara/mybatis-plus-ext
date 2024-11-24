package org.dromara.mpe.demo.autotable;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.demo.autotable.mysql.MysqlTable;
import org.dromara.mpe.demo.autotable.mysql.MysqlTableRepository;
import org.dromara.mpe.demo.autotable.pgsql.PgsqlTable;
import org.dromara.mpe.demo.autotable.pgsql.PgsqlTableRepository;
import org.dromara.mpe.demo.autotable.sqlite.Sqlite3Table;
import org.dromara.mpe.demo.autotable.sqlite.Sqlite3TableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@EnableAutoTableTest
@SpringBootTest()
public class AutoTableTest {

    @Autowired
    private MysqlTableRepository mysqlTableRepository;
    @Autowired
    private PgsqlTableRepository pgsqlTableRepository;
    @Autowired
    private Sqlite3TableRepository sqlite3TableRepository;

    @Test
    public void sqlite() {
        Sqlite3Table entity = new Sqlite3Table();
        entity.setUsername("sqlite");
        entity.setPhone("1234567890");
        sqlite3TableRepository.save(entity);

        List<Sqlite3Table> list = this.sqlite3TableRepository.list();
        assert !list.isEmpty();
    }

    @Test
    public void pgsql() {
        PgsqlTable entity = new PgsqlTable();
        entity.setPhone("1234567890");
        entity.setMoney(BigDecimal.ONE);
        pgsqlTableRepository.save(entity);

        List<PgsqlTable> list = this.pgsqlTableRepository.list();
        assert !list.isEmpty();
    }

    @Test
    public void mysql() {
        MysqlTable entity = new MysqlTable();
        entity.setUsername("mysql");
        mysqlTableRepository.save(entity);

        List<MysqlTable> list = mysqlTableRepository.list();
        assert !list.isEmpty();
    }
}
