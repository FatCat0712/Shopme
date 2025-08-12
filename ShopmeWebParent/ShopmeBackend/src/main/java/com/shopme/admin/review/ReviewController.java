package com.shopme.admin.review;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.review.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReviewController {
    private final ReviewService reviewService;

    private final String defaultURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=asc";

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews")
    public String listFirstPage() {
        return defaultURL;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(moduleURL = "/reviews", listName = "listReviews") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") Integer pageNum
    ) {
        reviewService.listByPage(helper, pageNum);
        return "reviews/reviews";
    }

    @PostMapping("/reviews/save")
    public String saveReview(Review review, RedirectAttributes ra) {
        try {
            reviewService.saveReview(review);
            ra.addFlashAttribute("message", "The review has been saved");
        } catch (ReviewNotFound e) {
           ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultURL;
    }

    @GetMapping("/reviews/detail/{id}")
    public String viewReviewDetails(@PathVariable(name = "id") Integer id, RedirectAttributes ra, Model model) {
        try {
            Review review = reviewService.getReview(id);
            model.addAttribute("review", review);
            return "reviews/review_details_readonly";
        } catch (ReviewNotFound e) {
           ra.addFlashAttribute("errorMessage", e.getMessage());
           return defaultURL;
        }
    }

    @GetMapping("/reviews/edit/{id}")
    public String editReview(@PathVariable(name = "id") Integer id, RedirectAttributes ra, Model model) {
        try {
            Review review = reviewService.getReview(id);
            model.addAttribute("review", review);
            model.addAttribute("id", id);
            return "reviews/review_form";
        } catch (ReviewNotFound e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return defaultURL;
        }
    }

    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
        try {
            reviewService.deleteReview(id);
            ra.addFlashAttribute("message", String.format("The review with ID %d has been deleted", id));
        } catch (ReviewNotFound e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultURL;
    }
}
