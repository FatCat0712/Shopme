package com.shopme.brand;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.brand.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.shopme.brand.BrandService.BRAND_PER_PAGE;

@Controller
public class BrandController {
    private final BrandService brandService;
    private final CategoryService categoryService;

    @Autowired
    public BrandController(BrandService brandService, CategoryService categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("/brands")
    public String listFirstPage(Model model) {
        return listBrandsByPage(1, null, model);
    }


    @GetMapping("/brands/page/{pageNum}")
    public String listBrandsByPage(
            @PathVariable(name = "pageNum") Integer pageNum,
            @RequestParam(name = "category", required = false) String categoryAlias,
            Model model
     ) {
           Page<Brand> page= brandService.listBrandsByCategory(categoryAlias, pageNum);
           List<Brand> listBrands = page.getContent();
           List<Category> first8CategoryList = categoryService.listFirst8Categories();
           List<Category> categoryList = categoryService.listRemainingCategories();

           int startCount = (pageNum - 1) * BRAND_PER_PAGE + 1;
           long endCount = startCount + BRAND_PER_PAGE - 1;


           model.addAttribute("startCount", startCount);
           model.addAttribute("endCount", endCount);
           model.addAttribute("currentPage", pageNum);
           model.addAttribute("listBrands", listBrands);
           model.addAttribute("totalPages", page.getTotalPages());
           model.addAttribute("totalItems", page.getTotalElements());
           model.addAttribute("first8CategoryList", first8CategoryList);
           model.addAttribute("categoryList", categoryList);
           return "brand/brands";
    }







}
