package com.shopme.product;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable(name = "category_alias") String alias, Model model) throws CategoryNotFoundException {
        return  viewCategoryByPage(alias, 1, model);
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

            int startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
            long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;

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

    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {
        try {
            Product product = productService.getProduct(alias);
            List<Category> listByCategoryParents =  categoryService.getCategoryParents(product.getCategory());
            model.addAttribute("listCategoryParents", listByCategoryParents);
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
            model.addAttribute("keyword", keyword);
            model.addAttribute("listResult", listResult);


            return "product/search_result";
    }

}
