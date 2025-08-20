package com.shopme.admin.section;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SectionProductSearchController {
    private final ProductService productService;

    @Autowired
    public SectionProductSearchController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/sections/search_product")
    public String showSearchProductPage(Model model) {
        return "redirect:/sections/search_product/page/1?sortField=name&sortDir=asc";
    }

    @PostMapping("/sections/search_product")
    public String searchProducts(@RequestParam(name = "keyword") String keyword) {
        return "redirect:/sections/search_product/page/1?sortField=name&sortDir=asc&keyword=" + keyword;
    }

    @GetMapping("/sections/search_product/page/{pageNum}")
    public String searchProductsByPage(
            @PagingAndSortingParam(listName = "listProducts", moduleURL = "/sections/search_product") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") Integer pageNum
    )
    {
            productService.searchProducts(pageNum, helper);
            return "sections/search_product";
    }
}
