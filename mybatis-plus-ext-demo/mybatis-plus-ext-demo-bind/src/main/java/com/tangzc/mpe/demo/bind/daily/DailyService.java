package com.tangzc.mpe.demo.bind.daily;

import com.tangzc.mpe.demo.bind.daily.repository.DailyRepository;
import com.tangzc.mpe.demo.bind.user.User;
import com.tangzc.mpe.demo.bind.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
public class DailyService {

    @Autowired
    private DailyRepository dailyRepository;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("daily/list")
    public List<Daily> list() {
        return dailyRepository.lambdaQueryPlus().bindList();
    }

    @GetMapping("daily/saveBatch")
    public List<Daily> saveBatch() {
        Daily daily = new Daily();
        daily.setContent(LocalDateTime.now().toString());
        User user1 = new User().setName("11111");
        userMapper.insert(user1);
        daily.setSubmitter(user1.getId());

        Daily daily2 = new Daily();
        daily2.setContent(LocalDateTime.now().toString());
        User user2 = new User().setName("22222");
        userMapper.insert(user2);
        daily2.setSubmitter(user2.getId());

        dailyRepository.saveBatch(Arrays.asList(daily, daily2));
        return dailyRepository.lambdaQueryPlus().bindList();
    }

    @GetMapping("bind/getById")
    public Daily getById() {
        return dailyRepository.getById("1");
    }
}
