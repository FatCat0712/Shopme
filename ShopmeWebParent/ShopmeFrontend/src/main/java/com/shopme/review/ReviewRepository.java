package com.shopme.review;

import com.shopme.common.entity.review.Review;
import com.shopme.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1")
    Page<Review> findByCustomer(int customerId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1 " +
            "AND (r.product.name LIKE %?2% " +
            "OR r.headline LIKE %?2% " +
            "OR r.comment LIKE %?2% )")
    Page<Review> findByCustomer(int customerId, String keyword, Pageable pageable);

    @Query("SELECT r FROM Review r " +
            "WHERE r.customer.id = ?1 " +
            "AND r.id = ?2")
    Review findByCustomer(int customerId, int reviewId);

    Page<Review> findByProduct(Product product, Pageable pageable);

    @Query("SELECT COUNT(r.id) FROM Review r " +
            "WHERE r.customer.id = ?1 " +
            "AND r.product.id = ?2")
    Long countByCustomerAndProduct(Integer customerId, Integer productId);

    @Query("UPDATE Review r SET r.votes = (SELECT SUM (rv.votes) FROM ReviewVote rv WHERE rv.review.id = ?1) WHERE r.id = ?1")
    @Modifying
    void updateVoteCount(Integer reviewId);
}
