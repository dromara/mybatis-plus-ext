package org.dromara.mpe.demo.autotable.sqlite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author don
 */
@RestController
@RequestMapping("autotable/sqlite")
public class SqliteTableController {

    @Autowired
    private Sqlite3TableRepository sqlite3TableRepository;

    @GetMapping("add")
    public void add(String username) {
        Sqlite3Table entity = new Sqlite3Table();
        entity.setUsername(username);
        sqlite3TableRepository.save(entity);
    }

    @GetMapping("list")
    public List<Sqlite3Table> list() {
        return sqlite3TableRepository.list();
    }
}
