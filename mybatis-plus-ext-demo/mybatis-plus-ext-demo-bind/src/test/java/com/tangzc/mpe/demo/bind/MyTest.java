package com.tangzc.mpe.demo.bind;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tangzc.mpe.bind.Binder;
import com.tangzc.mpe.demo.bind.user.User;
import com.tangzc.mpe.demo.bind.user.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(classes= DemoBindApplication.class)
public class MyTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void testAggFunc() {

        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class);
        List<User> users = userMapper.selectList(queryWrapper);
        Binder.bindOn(users, User::getHobbyNum);
        System.out.println(users.size());
    }
}
