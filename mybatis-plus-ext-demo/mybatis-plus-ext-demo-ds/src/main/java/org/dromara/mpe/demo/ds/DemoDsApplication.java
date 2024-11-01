package org.dromara.mpe.demo.ds;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tangzc.autotable.springboot.EnableAutoTable;
import org.dromara.mpe.demo.ds.entity.TestTable;
import org.dromara.mpe.demo.ds.entity.TestTable2;
import org.dromara.mpe.demo.ds.entity.TestTable2Mapper;
import org.dromara.mpe.demo.ds.entity.TestTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

@EnableAutoTable
@SpringBootApplication
public class DemoDsApplication {

    @Autowired
    private TestTableRepository testTableRepository;
    @Autowired
    private TestTable2Mapper testTable2Repository;

    public static void main(String[] args) {
        SpringApplication.run(DemoDsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void test() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<TestTable> testTables = testTableRepository.lambdaQueryPlus().bindList();

        for (TestTable testTable : testTables) {
            System.out.println(testTable.toString());
        }

        List<TestTable2> testTable2s = testTable2Repository.selectList(new QueryWrapper<>());

        for (TestTable2 testTable : testTable2s) {
            System.out.println(testTable.toString());
        }
    }
}
