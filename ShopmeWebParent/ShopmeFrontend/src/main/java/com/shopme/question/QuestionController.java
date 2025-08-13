package com.shopme.question;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.question.Question;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.product.ProductService;
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
    private final ProductService productService;
    private final ControllerHelper controllerHelper;

    @Autowired
    public QuestionController(QuestionService questionService, ProductService productService, ControllerHelper controllerHelper) {
        this.questionService = questionService;
        this.productService = productService;
        this.controllerHelper = controllerHelper;
    }

    @GetMapping("/questions/{product_alias}/page/{pageNum}")
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

            Page<Question> page = questionService.listByProduct(product, pageNum, sortField, sortDir);
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
