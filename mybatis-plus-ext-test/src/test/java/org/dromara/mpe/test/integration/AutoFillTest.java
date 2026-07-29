package org.dromara.mpe.test.integration;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.test.autofill.DeptNameAutoFillHandler;
import org.dromara.mpe.test.autofill.User;
import org.dromara.mpe.test.autofill.UserNameAutoFillHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoTableTest
@SpringBootTest
class AutoFillTest {

    @Autowired
    private org.dromara.mpe.test.autofill.UserRepository userRepository;

    private final String id = "1";

    @BeforeEach
    void init() {
        User user = new User();
        user.setId(id);
        userRepository.save(user);
    }

    @Test
    void testAutoFillHandlers() {
        User user = userRepository.getById(id);
        assertNotNull(user);
        assertEquals(DeptNameAutoFillHandler.DEPT_NAME, user.getDeptName());
        assertEquals(UserNameAutoFillHandler.USER_NAME, user.getName());
        assertNotNull(user.getRegisteredDate(), "注册时间应被自动填充");
    }
}
