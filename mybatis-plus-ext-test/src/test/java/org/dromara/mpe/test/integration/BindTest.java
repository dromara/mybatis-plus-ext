package org.dromara.mpe.test.integration;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.bind.Binder;
import org.dromara.mpe.test.bind.Article;
import org.dromara.mpe.test.bind.ArticleRepository;
import org.dromara.mpe.test.bind.BindUser;
import org.dromara.mpe.test.bind.BindUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoTableTest
@SpringBootTest
class BindTest {

    @Autowired
    private BindUserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    void init() {
        BindUser user1 = new BindUser().setId("1").setName("用户A");
        BindUser user2 = new BindUser().setId("2").setName("用户B");
        userRepository.saveOrUpdateBatch(Arrays.asList(user1, user2), 2);

        List<Article> list = Arrays.asList(
                new Article().setContent(LocalDateTime.now().toString()).setSubmitter(user1.getId()),
                new Article().setContent(LocalDateTime.now().toString()).setSubmitter(user2.getId())
        );
        articleRepository.saveBatch(list, list.size());
    }

    @Test
    void testNormalBind() {
        List<Article> articles = articleRepository.list();
        Binder.bindOn(articles, Article::getSubmitterUser, Article::getRegisteredDate);

        assertEquals(2, articles.size());

        Set<String> ids = articles.stream()
                .map(Article::getSubmitterUser)
                .map(BindUser::getId)
                .collect(Collectors.toSet());
        assertTrue(ids.containsAll(Arrays.asList("1", "2")));

        articles.forEach(article -> {
            assertNotNull(article.getSubmitterUser(), "关联用户不应为空");
            assertNotNull(article.getRegisteredDate(), "绑定字段不应为空");
        });
    }
}
