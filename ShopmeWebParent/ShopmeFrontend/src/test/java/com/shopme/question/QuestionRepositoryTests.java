package com.shopme.question;

import com.shopme.common.entity.question.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class QuestionRepositoryTests {
    private final QuestionRepository repo;

    @Autowired
    public QuestionRepositoryTests(QuestionRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testFindByProduct() {
        int productId = 59;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> page = repo.findByProduct(productId, pageable);
        List<Question> listQuestions = page.getContent();
        assertThat(listQuestions.size()).isGreaterThan(0);
        listQuestions.forEach(System.out::println);
    }
}
