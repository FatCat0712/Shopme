package com.shopme.admin.question;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.question.Question;
import com.shopme.common.entity.User;
import com.shopme.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void saveQuestion() {
        int productId = 59;
        int customerId = 7;

        Question question = new Question();
        question.setProduct(new Product(productId));
        question.setAsker(new Customer(customerId));
        question.setAskTime(new Date());
        question.setQuestionContent("Is thi computer touch screen ?");
        question.setApprovalStatus(false);

        Question savedQuestion = repo.save(question);
        assertThat(savedQuestion.getId()).isGreaterThan(0);
    }

    @Test
    public void listQuestions() {
        List<Question> listQuestions = repo.findAll();
        assertThat(listQuestions.size()).isGreaterThan(0);
        listQuestions.forEach(System.out::println);
    }

    @Test
    public void updateQuestions() {
        int questionId = 1;
        int userId = 21;
        String answerContent = "The country of origin is China";
        Optional<Question> question = repo.findById(questionId);
        assertTrue(question.isPresent());
        Question savedQuestion = question.get();
        savedQuestion.setAnswerContent(answerContent);
        savedQuestion.setAnswerer(new User(userId));
        savedQuestion.setApprovalStatus(true);
        savedQuestion.setAnswerTime(new Date());

        Question updatedQuestion = repo.save(savedQuestion);
        assertThat(updatedQuestion.getAnswerContent()).isEqualTo(answerContent);
        assertThat(updatedQuestion.getAnswerer().getId()).isEqualTo(userId);
    }

    @Test
    public void deleteQuestion() {
        int questionId = 5;
        repo.deleteById(questionId);
        Optional<Question> question = repo.findById(questionId);
        assertThat(question.isEmpty()).isTrue();
    }


}
