package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryRestController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories/check_unique")
    public String checkUnique(@RequestParam(name = "id", required = false) Integer id, @RequestParam("name") String name, @RequestParam("alias") String alias) {
        return categoryService.checkUnique(id, name, alias);
    }

    @GetMapping("/categories/list_categories_in_form")
    public List<Category> listCategoriesUsedInForm() {
        return categoryService.listCategoriesUsedInForm();
    }
}
