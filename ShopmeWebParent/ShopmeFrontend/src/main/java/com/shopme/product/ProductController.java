package com.shopme.product;

import com.shopme.ControllerHelper;
import com.shopme.brand.BrandService;
import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.brand.Brand;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.question.Question;
import com.shopme.common.entity.review.Review;
import com.shopme.common.exception.BrandNotFoundException;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.question.QuestionService;
import com.shopme.question.vote.QuestionVoteService;
import com.shopme.review.ReviewService;
import com.shopme.review.vote.ReviewVoteService;
import jakarta.servlet.http.HttpServletRequest;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.shopme.product.ProductService.PRODUCTS_PER_PAGE;

@Controller
public class ProductController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final BrandService brandService;
    private final ReviewService reviewService;
    private final ReviewVoteService reviewVoteService;
    private final ControllerHelper controllerHelper;
    private final QuestionService questionService;
    private final QuestionVoteService questionVoteService;


    @Autowired
    public ProductController(
            CategoryService categoryService, ProductService productService,
            BrandService brandService, ReviewService reviewService,
            ReviewVoteService reviewVoteService, ControllerHelper controllerHelper,
            QuestionService questionService, QuestionVoteService questionVoteService
    ) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.brandService = brandService;
        this.reviewService = reviewService;
        this.reviewVoteService = reviewVoteService;
        this.controllerHelper = controllerHelper;
        this.questionService = questionService;
        this.questionVoteService = questionVoteService;
    }

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return "redirect:/products/page/1";
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(
            @PathVariable(name = "pageNum") Integer pageNum,
            ProductCriteriaDTO productCriteriaDTO,
            HttpServletRequest request,
            Model model
    ) {
        Page<Product> page = productService.listAll(pageNum, productCriteriaDTO);

        List<Product> listProducts = page.getContent();
        List<Brand> listBrands = brandService.listAll();
        List<Category> listCategories = categoryService.listNoChildrenCategories();

        int startCount = (pageNum - 1) * PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + PRODUCTS_PER_PAGE - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }


        String qs = request.getQueryString();

        model.addAttribute("queryString", qs);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        return "product/products";


    }

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable(name = "category_alias") String alias, Model model) throws CategoryNotFoundException {
        return  viewCategoryByPage(alias, 1, model);
    }

    @GetMapping("/b/{brand_name}")
    public String viewBrandFirstPage(@PathVariable(name = "brand_name") String brandName, Model model){
        return  viewBrandByPage(brandName, 1, model);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable(name = "category_alias") String alias, @PathVariable(name = "pageNum") Integer pageNum,   Model model) {
        try {
            Category category = categoryService.getCategory(alias);
            List<Category> listByCategoryParents =  categoryService.getCategoryParents(category);

            Page<Product> page = productService.listByCategory(pageNum, category.getId());

            long totalItems = page.getTotalElements();
            int totalPages = page.getTotalPages();
            List<Product> listProducts = page.getContent();

            int startCount = (pageNum - 1) * PRODUCTS_PER_PAGE + 1;
            long endCount = startCount + PRODUCTS_PER_PAGE - 1;

            if(endCount >= totalItems) {
                endCount = totalItems;
            }

            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("listCategoryParents", listByCategoryParents);
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", pageNum);
            model.addAttribute("listProducts", listProducts);
            model.addAttribute("category", category);

            return "product/products_by_category";
        }catch (CategoryNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/b/{brand_name}/page/{pageNum}")
    public String viewBrandByPage(@PathVariable(name = "brand_name") String brandName, @PathVariable(name = "pageNum") Integer pageNum,   Model model) {
        try {
            Brand brand = brandService.findByName(brandName);

            Page<Product> page = productService.listByBrand(pageNum, brand.getId());

            long totalItems = page.getTotalElements();
            int totalPages = page.getTotalPages();
            List<Product> listProducts = page.getContent();

            int startCount = (pageNum - 1) * PRODUCTS_PER_PAGE + 1;
            long endCount = startCount + PRODUCTS_PER_PAGE - 1;

            if (endCount >= totalItems) {
                endCount = totalItems;
            }

            model.addAttribute("pageTitle", brand.getName());
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", pageNum);
            model.addAttribute("listProducts", listProducts);
            model.addAttribute("brand", brand);

            return "product/products_by_brand";
        } catch (BrandNotFoundException e) {
            return "error/404";
        }
    }


    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(HttpServletRequest request, @PathVariable("product_alias") String alias, Model model) {
        try {
            Product product = productService.get(alias);
            List<Product> listRelatedProducts = productService.listRelatedProductByCategory(product.getCategory().getId(), product.getId());

            String cleanedShortDescription = Jsoup.clean(product.getShortDescription(), Safelist.relaxed());
            String cleanedFullDescription = Jsoup.clean(product.getFullDescription(), Safelist.relaxed());

            product.setShortDescription(cleanedShortDescription);
            product.setFullDescription(cleanedFullDescription);

            List<Category> listByCategoryParents =  categoryService.getCategoryParents(product.getCategory());
            Page<Review> listReviews = reviewService.list3MostRecentReviewsByProduct(product);
            Page<Question> listQuestions = questionService.list3MostVotedQuestions(product);
            Integer answeredQuestions = questionService.countOfAnsweredQuestions(product);

            Customer customer = null;
            try {
                customer = controllerHelper.getAuthenticatetdCustomer(request);
            } catch (CustomerNotFoundException ignored) {

            }

            if(customer != null) {
                boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
                reviewVoteService.markReviewsVotedProductByCustomer(listReviews.getContent(), product.getId(), customer.getId());
                questionVoteService.markQuestionsVotedForProductByCustomer(listQuestions.getContent(), product, customer);


                if(customerReviewed) {
                    model.addAttribute("customerReviewed", true);
                }
                else {
                    boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
                    model.addAttribute("customerCanReview", customerCanReview);
                }
            }

            model.addAttribute("listCategoryParents", listByCategoryParents);
            model.addAttribute("listReviews", listReviews);
            model.addAttribute("listQuestions", listQuestions);
            model.addAttribute("answeredQuestions", answeredQuestions);

            model.addAttribute("listRelatedProducts", listRelatedProducts);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", product.getShortName());

            return "product/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }


    @GetMapping("/search")
    public String searchFirstPage(@RequestParam("keyword") String keyword, Model model) {
        return searchByPage(keyword,1, model);
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchByPage(@RequestParam("keyword") String keyword,@PathVariable("pageNum") int pageNum ,Model model) {
            Page<Product> page = productService.search(keyword, pageNum);

            List<Product> listResult = page.getContent();
            List<Brand> listBrands = brandService.listAll();
            List<Category> listCategories = categoryService.listNoChildrenCategories();

            long totalItems = page.getTotalElements();
            int totalPages = page.getTotalPages();

            int startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
            long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;

            if(endCount >= totalItems) {
                endCount = totalItems;
            }

            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", pageNum);
            model.addAttribute("pageTitle", keyword + " - Search Result");
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("keyword", keyword);
            model.addAttribute("listResult", listResult);


            return "product/search_result";
    }







}
