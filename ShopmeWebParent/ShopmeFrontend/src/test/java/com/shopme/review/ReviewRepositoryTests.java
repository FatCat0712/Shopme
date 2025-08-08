package com.shopme.review;

import com.shopme.common.entity.Review;
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
public class ReviewRepositoryTests {
    private final ReviewRepository repo;

    @Autowired
    public ReviewRepositoryTests(ReviewRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testFindByCustomerNoKeyword() {
        int customerId = 5;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> page = repo.findByCustomer(customerId, pageable);

        List<Review> listReviewsByCustomerId = page.getContent();
        assertThat(listReviewsByCustomerId.size()).isGreaterThan(0);
        listReviewsByCustomerId.forEach(System.out::println);
    }

    @Test
    public void testFindByCustomerWithKeyword() {
        int customerId = 5;
        String keyword = "great";
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> page = repo.findByCustomer(customerId,keyword, pageable);

        List<Review> listReviewsByCustomerId = page.getContent();
        assertThat(listReviewsByCustomerId.size()).isGreaterThan(0);
        listReviewsByCustomerId.forEach(System.out::println);
    }

    @Test
    public void testFindByCustomerAndReviewId() {
        int customerId = 5;
        int reviewId = 12;
        Review review = repo.findByCustomer(customerId, reviewId);
        assertThat(review).isNotNull();
        System.out.println(review);

    }
}
