package com.shopme.review.vote;

import com.shopme.common.entity.review.ReviewVote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewVoteRepository extends CrudRepository<ReviewVote, Integer> {
    @Query("SELECT rv FROM ReviewVote rv WHERE rv.review.id = ?1 AND rv.customer.id = ?2")
    ReviewVote findByReviewAndCustomer(Integer reviewId, Integer customerId);

    @Query("SELECT rv FROM ReviewVote rv WHERE rv.review.product.id = ?1 AND rv.customer.id = ?2")
    List<ReviewVote> findByProductAndCustomer(Integer productId, Integer customerId);


}
