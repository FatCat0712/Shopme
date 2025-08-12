package com.shopme.review;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.review.Review;
import com.shopme.common.entity.review.ReviewVote;
import com.shopme.review.vote.ReviewVoteRepository;
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
public class ReviewVoteRepositoryTests {
    private final ReviewVoteRepository repo;

    @Autowired
    public ReviewVoteRepositoryTests(ReviewVoteRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testSaveVote() {
        int reviewId = 4;
        int customerId = 43;

        Review review = new Review(reviewId);
        Customer customer = new Customer(customerId);

        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setReview(review);
        reviewVote.setCustomer(customer);
        reviewVote.voteDown();

        ReviewVote savedVote = repo.save(reviewVote);
        assertThat(savedVote.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByReviewAndCustomer() {
        Integer reviewId = 57;
        Integer customerId = 43;
        ReviewVote vote = repo.findByReviewAndCustomer(reviewId, customerId);
        assertThat(vote.getId()).isGreaterThan(0);
        System.out.println(vote);
    }

    @Test
    public void testFindByProductAndCustomer() {
        Integer productId = 1;
        Integer customerId = 43;
        List<ReviewVote> listVotes = repo.findByProductAndCustomer(productId, customerId);
        assertThat(listVotes.size()).isGreaterThan(0);
        listVotes.forEach(System.out::println);
    }


}
