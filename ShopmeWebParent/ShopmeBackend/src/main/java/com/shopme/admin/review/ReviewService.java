package com.shopme.admin.review;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository repo;

    public static final int REVIEW_PER_PAGE = 5;

    @Autowired
    public ReviewService(ReviewRepository repo) {
        this.repo = repo;
    }

    public void listByPage(PagingAndSortingHelper helper, int pageNum) {
        helper.listEntities(pageNum, REVIEW_PER_PAGE, repo);
    }

    public Review getReview(int id) throws ReviewNotFound {
        Optional<Review> savedReview = repo.findById(id);
        if(savedReview.isEmpty()) {
            throw new ReviewNotFound("Could not find any review with ID " + id);
        }
       return savedReview.get();
    }

    public void saveReview(Review review) {
            repo.save(review);
    }

    public void deleteReview(int id) throws ReviewNotFound {
        Optional<Review> savedReview = repo.findById(id);
        if(savedReview.isEmpty()) {
            throw new ReviewNotFound("Could not find any review with ID " + id);
        }
        repo.deleteById(id);
    }



}
