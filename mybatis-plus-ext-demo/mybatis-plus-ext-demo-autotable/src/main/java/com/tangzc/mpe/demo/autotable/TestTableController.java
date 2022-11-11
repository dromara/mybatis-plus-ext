package com.tangzc.mpe.demo.autotable;

import com.tangzc.mpe.demo.autotable.entity.TestTable;
import com.tangzc.mpe.demo.autotable.repository.TestTableRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("actable")
public class TestTableController {

    @Resource
    private TestTableRepository testTableRepository;

    @GetMapping("add")
    public void add(String username) {
        TestTable entity = new TestTable();
        entity.setUserName(username);
        testTableRepository.save(entity);
    }
}
