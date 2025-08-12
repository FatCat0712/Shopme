package com.shopme.review;

import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.review.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void testingByProduct() {
        Product product = new Product(23);
        Pageable pageable = PageRequest.of(0, 3);
        Page<Review> page = repo.findByProduct(product, pageable);
        assertThat(page.getTotalElements()).isGreaterThan(0);
        List<Review> content = page.getContent();
        content.forEach(System.out::println);
    }

    @Test
    public void testCountByCustomerAndProduct() {
        Integer customerId = 99;
        Integer productId = 199;
        Long count = repo.countByCustomerAndProduct(customerId, productId);
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testUpdateCount() {
        Integer reviewId = 4;
        repo.updateVoteCount(reviewId);
        Optional<Review> review = repo.findById(reviewId);
        assertTrue(review.isPresent());
        Review savedReview = review.get();
        assertThat(savedReview.getVotes()).isEqualTo(1);
    }

    @Test
    public void testGetVoteCount() {
        Integer reviewId = 4;
        Integer voteCount = repo.getVoteCount(reviewId);
        assertThat(voteCount).isEqualTo(1);
    }
}
