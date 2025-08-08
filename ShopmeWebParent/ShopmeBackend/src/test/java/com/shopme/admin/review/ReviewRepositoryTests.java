package com.shopme.admin.review;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ReviewRepositoryTests {
    private final ReviewRepository repo;
    private final TestEntityManager testEntityManager;

    @Autowired
    public ReviewRepositoryTests(ReviewRepository repo, TestEntityManager testEntityManager) {
        this.repo = repo;
        this.testEntityManager = testEntityManager;
    }

    @Test
    public void testCreateReview() {
        Customer customer = new Customer(1);
        Product product = new Product(1);

        Review review = new Review();
        review.setHeadline("The product was corrupted");
        review.setComment("The product was broken and unusable");
        review.setReviewTime(new Date());
        review.setRating(1);
        review.setCustomer(customer);
        review.setProduct(product);

        Review newReview = repo.save(review);
        assertThat(newReview.getId()).isGreaterThan(0);
    }

    @Test
    public void testListReviews() {
        List<Review> listReviews = repo.findAll();
        assertThat(listReviews.size()).isGreaterThan(0);
        listReviews.forEach(System.out::println);
    }

    @Test
    public void testGetReview() {
        Optional<Review> savedReview = repo.findById(1);
        assertThat(savedReview).isNotEmpty();
    }

    @Test
    public void testUpdateReview() {
        String comment = "The quality of product was bad";
        Optional<Review> savedReview = repo.findById(1);
        assertTrue(savedReview.isPresent());

        Review review = savedReview.get();
        review.setComment(comment);

        Review updatedReview = repo.save(review);
        assertThat(updatedReview.getComment()).isEqualTo(comment);
    }

    @Test
    public void testDeleteReview() {
        int id = 1;
        repo.deleteById(id);
        Optional<Review> savedReview = repo.findById(id);
        assertThat(savedReview).isEmpty();
    }

}
