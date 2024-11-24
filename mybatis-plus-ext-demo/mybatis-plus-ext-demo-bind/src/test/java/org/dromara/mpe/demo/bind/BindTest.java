package org.dromara.mpe.demo.bind;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.bind.Binder;
import org.dromara.mpe.demo.bind.normal.Article;
import org.dromara.mpe.demo.bind.normal.ArticleRepository;
import org.dromara.mpe.demo.bind.normal.User;
import org.dromara.mpe.demo.bind.normal.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@EnableAutoTableTest
@SpringBootTest(classes = DemoBindApplication.class)
public class BindTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    public void init() {

        User user1 = new User().setId("1").setName("11111");
        User user2 = new User().setId("2").setName("22222");
        userRepository.saveOrUpdateBatch(Arrays.asList(user1, user2), 2);

        List<Article> list = Arrays.asList(
                new Article().setContent(LocalDateTime.now().toString())
                        .setSubmitter(user1.getId()),
                new Article().setContent(LocalDateTime.now().toString())
                        .setSubmitter(user2.getId())
        );
        articleRepository.saveBatch(list, list.size());
    }

    /**
     * 普通的绑定方式
     */
    @Test
    public void normalBind() {

        List<Article> dailies = articleRepository.list();
        Binder.bindOn(dailies, Article::getSubmitterUser, Article::getRegisteredDate);
        System.out.println(dailies.size());
        for (Article daily : dailies) {
            System.out.println();
            System.out.println(daily.getSubmitterUser().getName());
            System.out.println(daily.getRegisteredDate());
        }
    }
}
