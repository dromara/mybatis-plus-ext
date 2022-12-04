package com.tangzc.mpe.demo.autotable.mysql;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("autotable/mysql")
public class MysqlTableController {

    @Resource
    private MysqlTableRepository mysqlTableRepository;

    @GetMapping("add")
    public void add(String username) {
        MysqlTable entity = new MysqlTable();
        entity.setUsername(username);
        mysqlTableRepository.save(entity);
    }

    @GetMapping("list")
    public List<MysqlTable> list() {
        return mysqlTableRepository.list();
    }
}
