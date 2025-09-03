package com.shopme.admin.review;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.review.Review;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final ProductRepository productRepo;

    public static final int REVIEW_PER_PAGE = 8;

    @Autowired
    public ReviewService(ReviewRepository reviewRepo, ProductRepository productRepo) {
        this.reviewRepo = reviewRepo;
        this.productRepo = productRepo;
    }

    public List<Review> listAll() {
        return reviewRepo.findAll();
    }

    public void listByPage(PagingAndSortingHelper helper, int pageNum) {
        helper.listEntities(pageNum, REVIEW_PER_PAGE, reviewRepo);
    }

    public Review getReview(int id) throws ReviewNotFound {
        Optional<Review> savedReview = reviewRepo.findById(id);
        if(savedReview.isEmpty()) {
            throw new ReviewNotFound("Could not find any review with ID " + id);
        }
       return savedReview.get();
    }

    public void saveReview(Review reviewInForm) throws ReviewNotFound {
        int productId;
        if(reviewInForm.getId() == 0) {
            productId = reviewInForm.getProduct().getId();
            reviewRepo.save(reviewInForm);
        }
        else {
            Review reviewInDB = reviewRepo.findById(reviewInForm.getId())
                    .orElseThrow(() -> new ReviewNotFound("Review ID " + reviewInForm.getId() + " not found"));
            productId = reviewInDB.getProduct().getId();
            reviewInDB.setHeadline(reviewInForm.getHeadline());
            reviewInDB.setComment(reviewInForm.getComment());
            reviewRepo.save(reviewInDB);
        }
        productRepo.updateReviewCountAndAverageRating(productId);
    }

    public void deleteReview(int id) throws ReviewNotFound {
        Optional<Review> savedReview = reviewRepo.findById(id);
        if(savedReview.isEmpty()) {
            throw new ReviewNotFound("Could not find any review with ID " + id);
        }
        reviewRepo.deleteById(id);
    }

    public Integer countReviewProducts() {
        return listAll().stream().map(p -> p.getProduct().getId()).distinct().toList().size();
    }



}
