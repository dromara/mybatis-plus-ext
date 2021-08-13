package com.tangzc.mpe.demo.condition;

import com.tangzc.mpe.demo.condition.daily.Daily;
import com.tangzc.mpe.demo.condition.daily.DailyRepository;
import com.tangzc.mpe.demo.condition.user.User;
import com.tangzc.mpe.demo.condition.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoConditionApplication.class)
class DemoConditionApplicationTests {

    @Resource
    private UserRepository userRepository;
    @Resource
    private DailyRepository dailyRepository;

    @Test
    public void insert() {

        List<User> userList = new ArrayList<>();
        userList.add(new User().setId("1").setName("zhangsan"));
        userList.add(new User().setId("2").setName("lisi"));
        userRepository.saveBatch(userList);

        List<Daily> dailyList = new ArrayList<>();
        dailyList.add(new Daily().setContent("日志1"));
        dailyList.add(new Daily().setContent("日志2"));
        dailyRepository.saveBatch(dailyList);
    }

}
