package com.tangzc.mpe.demo.autotable.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("autotable/mysql")
public class MysqlTableController {

    @Autowired
    private MysqlTableRepository mysqlTableRepository;

    @GetMapping("add")
    public void add(String username) {
        MysqlTable3 entity = new MysqlTable3();
        entity.setName(username);
        mysqlTableRepository.save(entity);
    }

    @GetMapping("list")
    public List<MysqlTable3> list() {
        return mysqlTableRepository.list();
    }
}
