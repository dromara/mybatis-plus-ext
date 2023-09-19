package com.tangzc.mpe.demo.autotable.pgsql;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("autotable/pgsql")
public class PgsqlTableController {

    @Resource
    private PgsqlTableRepository pgsqlTableRepository;

    @GetMapping("add")
    public void add(String username) {
        PgsqlTable entity = new PgsqlTable();
        entity.setUsername(username);
        this.pgsqlTableRepository.save(entity);
    }

    @GetMapping("list")
    public List<PgsqlTable> list() {
        return this.pgsqlTableRepository.list();
    }
}
