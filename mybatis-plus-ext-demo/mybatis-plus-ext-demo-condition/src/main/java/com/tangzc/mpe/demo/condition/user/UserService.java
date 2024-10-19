package com.tangzc.mpe.demo.condition.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("adduser")
    public void add(String name) {

        userRepository.save(new User().setName(name));
    }
}
