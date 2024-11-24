package org.dromara.mpe.demo.autotable;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.demo.autotable.sqlite.Sqlite3Table;
import org.dromara.mpe.demo.autotable.sqlite.Sqlite3TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@EnableAutoTableTest(basePackages = "org.dromara.mpe.demo.autotable.sqlite")
@SpringBootTest()
public class SqliteTableTest {

    @Autowired
    private Sqlite3TableRepository sqlite3TableRepository;

    // @Test
    public void test() {
        Sqlite3Table entity = new Sqlite3Table();
        entity.setUsername("sqlite");
        entity.setPhone("1234567890");
        sqlite3TableRepository.save(entity);

        List<Sqlite3Table> list = this.sqlite3TableRepository.list();
        assert !list.isEmpty();
    }
}
