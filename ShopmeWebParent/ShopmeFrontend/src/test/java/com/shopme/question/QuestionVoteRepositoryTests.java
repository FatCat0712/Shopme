package com.shopme.question;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.question.Question;
import com.shopme.common.entity.question.QuestionVote;
import com.shopme.question.vote.QuestionVoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class QuestionVoteRepositoryTests {
    private final QuestionVoteRepository repo;

    @Autowired
    public QuestionVoteRepositoryTests(QuestionVoteRepository repo) {
        this.repo = repo;
    }

    @Test
    public void saveQuestionVote() {
        int questionId = 9;
        int customerId = 43;

        Question question = new Question(questionId);
        Customer customer = new Customer(customerId);

        QuestionVote questionVote = new QuestionVote();
        questionVote.setQuestion(question);
        questionVote.setCustomer(customer);
        questionVote.voteDown();

        QuestionVote savedVote = repo.save(questionVote);
        assertThat(savedVote.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByQuestionAndCustomer() {
        Integer questionId = 9;
        Integer customerId = 43;
        QuestionVote vote = repo.findByQuestionAndCustomer(questionId, customerId);
        assertThat(vote.getId()).isGreaterThan(0);
        System.out.println(vote);
    }

    @Test
    public void testFindByProductAndCustomer() {
        Integer productId = 59;
        Integer customerId = 43;
        List<QuestionVote> listVotes = repo.findByProductAndCustomer(productId, customerId);
        assertThat(listVotes.size()).isGreaterThan(0);
        listVotes.forEach(System.out::println);
    }
}
