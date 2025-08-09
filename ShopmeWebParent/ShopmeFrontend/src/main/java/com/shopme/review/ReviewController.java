package com.shopme.review;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.customer.CustomerService;
import com.shopme.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.shopme.order.OrderService.ORDER_PER_PAGE;

@Controller
public class ReviewController {
    private final ReviewService reviewService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final String defaultURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";

    @Autowired
    public ReviewController(ReviewService reviewService, CustomerService customerService, ProductService productService) {
        this.reviewService = reviewService;
        this.customerService = customerService;
        this.productService = productService;
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
        Customer customer = getLoggedInCustomer(request);
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

        return "review/reviews_customer";
    }

    @GetMapping("/review/details/{id}")
    public String viewReview(HttpServletRequest request, @PathVariable(name = "id") Integer id, RedirectAttributes ra, Model model) {
        Customer customer = getLoggedInCustomer(request);
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
    public String listFirstReviewPage(@PathVariable(name = "product_alias") String productAlias, Model model) {
        try {
            Product product = productService.get(productAlias);
            int pageNum = 1;
            Page<Review> page = reviewService.listByProduct(product, pageNum, "reviewTime", "desc");
            List<Review> listReviews = page.getContent();


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
            model.addAttribute("sortField", "reviewTime");
            model.addAttribute("sortDir", "desc");
            return "review/review_product";

        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/ratings/{product_alias}/page/{pageNum}")
    public String listByPage(
            @PathVariable(name = "product_alias") String productAlias,
            @PathVariable(name = "pageNum") Integer pageNum,
            @RequestParam(name = "sortField", required = false) String sortField,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            Model model
    ) {
        try {
            Product product = productService.get(productAlias);

            Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
            List<Review> listReviews = page.getContent();

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
            model.addAttribute("sortField", "reviewTime");
            model.addAttribute("sortDir", "desc");
            return "review/review_product";

        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    private Customer getLoggedInCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
       return  customerService.findCustomerByEmail(email);
    }

}
