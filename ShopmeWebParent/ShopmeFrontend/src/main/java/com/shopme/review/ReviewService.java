package com.shopme.review;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.product.Product;
import com.shopme.order.OrderDetailRepository;
import com.shopme.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final OrderDetailRepository orderDetailRepo;
    private final ProductRepository productRepo;

    public static final int REVIEW_PER_PAGE = 5;

    @Autowired
    public ReviewService(ReviewRepository reviewRepo, OrderDetailRepository orderDetailRepo, ProductRepository productRepository) {
        this.reviewRepo = reviewRepo;
        this.orderDetailRepo = orderDetailRepo;
        this.productRepo = productRepository;
    }

    public Page<Review> listByCustomerByPage(Customer customer, int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        if("asc".equals(sortDir)) sort = sort.ascending();
        else if("desc".equals(sortDir)) sort = sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, REVIEW_PER_PAGE, sort);

        Page<Review> page;
        int customerId = customer.getId();
        if(keyword != null) {
            page = reviewRepo.findByCustomer(customerId, keyword ,pageable);
        }else {
            page = reviewRepo.findByCustomer(customerId, pageable);
        }
        return page;
    }

    public Review getByCustomerAndId(Customer customer, int reviewId) throws ReviewNotFoundException {
        int customerId = customer.getId();
        Review review =  reviewRepo.findByCustomer(customerId, reviewId);
        if(review == null) {
            throw new ReviewNotFoundException("Could not find any reviews with ID " + reviewId);
        }
        return review;
    }

    public Page<Review> list3MostRecentReviewsByProduct(Product product) {
        Sort sort = Sort.by("reviewTime").descending();
        Pageable pageable = PageRequest.of(0, 3, sort);
        return reviewRepo.findByProduct(product, pageable);
    }

    public Page<Review> listByProduct(Product product, int pageNum, String sortField, String sortDir) {
        Sort sort =Sort.by(sortField);
        if(sortDir.equals("asc")) sort = sort.ascending();
        else if(sortDir.equals("desc")) sort = sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, REVIEW_PER_PAGE , sort);
        return reviewRepo.findByProduct(product, pageable);
    }

    public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
         Integer customerId = customer.getId();
         Long count = reviewRepo.countByCustomerAndProduct(customerId, productId);
         return count > 0;
    }

    public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
        Integer customerId = customer.getId();
        Long count = orderDetailRepo.countByProductAndCustomerAndOrderStatus(productId, customerId, OrderStatus.DELIVERED);
        return count > 0;
    }

    public Review save(Review review) {
        review.setReviewTime(new Date());
        Review savedReview = reviewRepo.save(review);
        Integer productId = review.getProduct().getId();
        productRepo.updateReviewCountAndAverageRating(productId);
        return savedReview;
    }



}
