package com.tangzc.mpe.demo.autotable.sqlite;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("autotable/sqlite")
public class SqliteTableController {

    @Resource
    private SqliteTableRepository sqliteTableRepository;

    @GetMapping("add")
    public void add(String username) {
        SqliteTable entity = new SqliteTable();
        entity.setUsername(username);
        sqliteTableRepository.save(entity);
    }

    @GetMapping("list")
    public List<SqliteTable> list() {
        return sqliteTableRepository.list();
    }
}
