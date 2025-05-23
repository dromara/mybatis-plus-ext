package org.dromara.mpe.demo.condition;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.demo.condition.daily.Daily;
import org.dromara.mpe.demo.condition.daily.Daily2;
import org.dromara.mpe.demo.condition.daily.Daily2Repository;
import org.dromara.mpe.demo.condition.daily.DailyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@EnableAutoTableTest
@SpringBootTest
class DemoConditionApplicationTests {

    @Autowired
    private DailyRepository dailyRepository;
    @Autowired
    private Daily2Repository daily2Repository;

    @BeforeEach
    public void init() {
        List<Daily> dailyList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dailyList.add(new Daily().setContent("日志" + (i + 1)));
        }
        dailyRepository.saveBatch(dailyList, dailyList.size());
    }


    @Test
    public void randomList() {
        List<Daily> list = dailyRepository.list();
        for (Daily daily : list) {
            System.out.println(daily);
        }
    }


    @Test
    public void fixList() {
        List<Daily2> list = daily2Repository.list();
        for (Daily2 daily : list) {
            assert daily.getSubmitter().equals(FilterByFixedUser.fixId);
        }
    }

}
