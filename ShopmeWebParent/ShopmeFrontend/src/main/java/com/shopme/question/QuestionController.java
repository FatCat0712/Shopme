package com.shopme.question;

import com.shopme.ControllerHelper;
import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.question.Question;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.product.ProductService;
import com.shopme.question.vote.QuestionVoteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final CategoryService categoryService;
    private final QuestionVoteService questionVoteService;
    private final ProductService productService;
    private final ControllerHelper controllerHelper;

    @Autowired
    public QuestionController(
            QuestionService questionService, CategoryService categoryService,
            QuestionVoteService questionVoteService, ProductService productService,
            ControllerHelper controllerHelper
    ) {
        this.questionService = questionService;
        this.categoryService = categoryService;
        this.questionVoteService = questionVoteService;
        this.productService = productService;
        this.controllerHelper = controllerHelper;
    }

    @GetMapping("/questions")
    public String listFirstPage(HttpServletRequest request, Model model) {
        return listByPage(request, 1, "desc", null, model);
    }

    @GetMapping("/questions/page/{pageNum}")
    public String listByPage(
            HttpServletRequest request,
            @PathVariable(name = "pageNum") Integer pageNum,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model
    ) {
        Customer customer = null;
        try {
            customer = controllerHelper.getAuthenticatetdCustomer(request);
        } catch (CustomerNotFoundException e) {
            return "error/403";
        }
        Page<Question> page  = questionService.listByCustomer(customer, pageNum, sortDir, keyword);
        List<Question> listQuestions = page.getContent();
        long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();

        int startCount = (pageNum - 1) * QuestionService.QUESTION_PER_PAGE + 1;
        long endCount = startCount + QuestionService.QUESTION_PER_PAGE - 1;

        if(endCount >= totalItems) {
            endCount = totalItems;
        }

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listQuestions", listQuestions);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("sortField", "askTime");
        model.addAttribute("moduleURL", "/questions");

        return "question/questions_customer";
    }

    @GetMapping("/questions/detail/{id}")
    public String viewQuestionDetail(@PathVariable(name = "id") Integer questionId, Model model) {
        try {
            Question question = questionService.get(questionId);
            model.addAttribute("question", question);
        } catch (QuestionNotFound e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "question/question_detail_modal";
    }



    @GetMapping("/questions/{product_alias}/page/{pageNum}")
    public String listByProduct(
            @PathVariable(name = "product_alias") String productAlias,
            @PathVariable(name = "pageNum") Integer pageNum,
            @RequestParam(name = "sortField", required = false) String sortField,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            Model model,
            HttpServletRequest request
    ) {
        try {
            Product product = productService.get(productAlias);

           List<Category> listCategoryParents =  categoryService.getCategoryParents(product.getCategory());

            Page<Question> page = questionService.listByProduct(product, pageNum, sortField, sortDir);

            Customer customer = null;
            try {
                customer = controllerHelper.getAuthenticatetdCustomer(request);
            } catch (CustomerNotFoundException ignored) {

            }


            List<Question> listQuestions = page.getContent();

            if(customer != null) {
                questionVoteService.markQuestionsVotedForProductByCustomer(listQuestions, product, customer);
            }

            long totalItems = page.getTotalElements();
            int totalPages = page.getTotalPages();

            int startCount = (pageNum - 1) * QuestionService.QUESTION_PER_PAGE + 1;
            long endCount = startCount + QuestionService.QUESTION_PER_PAGE - 1;

            if(endCount >= totalItems) {
                endCount = totalItems;
            }

            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", pageNum);
            model.addAttribute("listQuestions", listQuestions);
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("product", product);
            model.addAttribute("productAlias", productAlias);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            return "question/question_product";

        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }
}
