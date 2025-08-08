package com.shopme.admin.review;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>, SearchRepository<Review, Integer> {

    @Query("SELECT r FROM Review r " +
            "WHERE r.product.name LIKE %?1% " +
            "OR CONCAT(r.customer.firstName,' ', r.customer.lastName) LIKE %?1% " +
            "OR r.headline LIKE %?1% " +
            "OR r.comment LIKE %?1%")
    Page<Review> findAll(String keyword, Pageable pageable);
}
