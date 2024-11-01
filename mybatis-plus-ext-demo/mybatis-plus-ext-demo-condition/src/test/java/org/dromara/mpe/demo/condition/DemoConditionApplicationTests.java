package org.dromara.mpe.demo.condition;

import org.dromara.mpe.demo.condition.daily.Daily;
import org.dromara.mpe.demo.condition.daily.DailyRepository;
import org.dromara.mpe.demo.condition.user.User;
import org.dromara.mpe.demo.condition.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = DemoConditionApplication.class)
class DemoConditionApplicationTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DailyRepository dailyRepository;

    @Test
    public void insertUser() {

        List<User> userList = new ArrayList<>();
        userList.add(new User().setId("1").setName("zhangsan"));
        userList.add(new User().setId("2").setName("lisi"));
        userRepository.saveBatch(userList);
    }

    @Test
    public void insertDaily() {

        List<Daily> dailyList = new ArrayList<>();
        dailyList.add(new Daily().setContent("日志1"));
        dailyList.add(new Daily().setContent("日志2"));
        dailyRepository.saveBatch(dailyList);
    }

}
