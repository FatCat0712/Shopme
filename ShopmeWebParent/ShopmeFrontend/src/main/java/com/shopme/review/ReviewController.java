package com.shopme.review;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.review.Review;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.product.ProductService;
import com.shopme.review.vote.ReviewVoteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.shopme.order.OrderService.ORDER_PER_PAGE;

@Controller
public class ReviewController {
    private final ReviewService reviewService;
    private final ProductService productService;
    private final ControllerHelper controllerHelper;
    private final ReviewVoteService voteService;
    private final String defaultURL = "redirect:/reviews/page/1?sortField=votes&sortDir=desc";

    @Autowired
    public ReviewController(
            ReviewService reviewService, ProductService productService,
                            ControllerHelper controllerHelper, ReviewVoteService voteService
    ) {
        this.reviewService = reviewService;
        this.productService = productService;
        this.controllerHelper = controllerHelper;
        this.voteService = voteService;
    }

    @GetMapping("/reviews")
    public String listFirstPage() {
        return defaultURL;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listReviewsByCustomerByPage(
            HttpServletRequest request,
            @PathVariable(name = "pageNum") Integer pageNum,
            @RequestParam(name = "sortField") String sortField,
            @RequestParam(name = "sortDir") String sortDir,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model
    ) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        Page<Review> page = reviewService.listByCustomerByPage(customer, pageNum, sortField, sortDir, keyword);

        List<Review> listReviews = page.getContent();

        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        int startCount = (pageNum - 1) * ORDER_PER_PAGE + 1;
        long endCount = startCount + ORDER_PER_PAGE - 1;

        if(endCount > totalItems) {
            endCount = totalItems;
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listReviews", listReviews);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("moduleURL", "/reviews");
        model.addAttribute("keyword", keyword);

        return "review/reviews_customer";
    }

    @GetMapping("/review/details/{id}")
    public String viewReview(HttpServletRequest request, @PathVariable(name = "id") Integer id, RedirectAttributes ra, Model model) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        try {
            Review review =reviewService.getByCustomerAndId(customer, id);
            model.addAttribute("review", review);
            return "review/review_detail_modal";
        } catch (ReviewNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return defaultURL;
        }
    }

    @GetMapping("/ratings/{product_alias}")
    public String listFirstReviewPage(@PathVariable(name = "product_alias") String productAlias, Model model, HttpServletRequest request) {
       return listByPage(productAlias, 1,"reviewTime", "desc", model, request);
    }

    @GetMapping("/ratings/{product_alias}/page/{pageNum}")
    public String listByPage(
            @PathVariable(name = "product_alias") String productAlias,
            @PathVariable(name = "pageNum") Integer pageNum,
            @RequestParam(name = "sortField", required = false) String sortField,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            Model model,
            HttpServletRequest request
    ) {
        try {
            Product product = productService.get(productAlias);

            Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
            List<Review> listReviews = page.getContent();

            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            if(customer != null) {
                voteService.markReviewsVotedProductByCustomer(listReviews, product.getId(), customer.getId());
            }

            long totalItems = page.getTotalElements();
            int totalPages = page.getTotalPages();

            int startCount = (pageNum - 1) * ReviewService.REVIEW_PER_PAGE + 1;
            long endCount = startCount + ReviewService.REVIEW_PER_PAGE - 1;

            if(endCount >= totalItems) {
                endCount = totalItems;
            }

            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", pageNum);
            model.addAttribute("listReviews", listReviews);
            model.addAttribute("product", product);
            model.addAttribute("productAlias", productAlias);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            return "review/review_product";

        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/write_review/product/{productId}")
    public String showReviewForm(@PathVariable(name = "productId") Integer productId, Model model, HttpServletRequest request) {
        Review review = new Review();
        try {
            Product product = productService.get(productId);
            model.addAttribute("review", review);
            model.addAttribute("product", product);

            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, productId);

            if(customerReviewed) {
                model.addAttribute("customerReviewed", true);
            }
            else {
                boolean canCustomerReview = reviewService.canCustomerReviewProduct(customer, productId);
                if(canCustomerReview) {
                    model.addAttribute("customerCanReview", true);
                }
                else {

                    model.addAttribute("noPreviewPermission", true);
                }
            }

            return "review/review_form";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    @PostMapping("/post_review")
    public String saveReview(Review review, Model model,@RequestParam(name = "productId") Integer productId, HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        try {
            Product product = productService.get(productId);
            review.setProduct(product);
            review.setCustomer(customer);
            Review savedReview = reviewService.save(review);
            model.addAttribute("review", savedReview);
            return "review/review_done";

        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

}
