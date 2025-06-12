package org.dromara.mpe.demo.autofill;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@EnableAutoTableTest
@SpringBootTest
public class AutoFillTest {

    @Autowired
    private UserRepository userRepository;
    private final String id = "1";

    @BeforeEach
    public void init() {
        User user = new User();
        user.setId(id);
        userRepository.save(user);
        System.out.println(user.toString());
    }

    /**
     * 普通的绑定方式
     */
    @Test
    public void check() {
        User user = userRepository.getById(id);
        System.out.println(user.toString());
        assert user.getDeptName().equals(DeptNameAutoFillHandler.deptName);
        assert user.getName().equals(UserNameAutoFillHandler.userName);
        assert user.getRegisteredDate() != null;
    }
}
