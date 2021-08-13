package com.tangzc.mpe.demo.condition.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class UserService {

    @Resource
    private UserRepository userRepository;

    @GetMapping("add")
    public void add(String name) {

        userRepository.save(new User().setName(name));
    }
}
