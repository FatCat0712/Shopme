package com.shopme.admin.category;

import com.shopme.admin.SupabaseS3Util;
import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listFirstPage(@RequestParam(name = "sortDir", required = false) String sortDir, Model model) {
            return listByPage(1, sortDir, null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(
            @PathVariable(name = "pageNum") Integer pageNum,
            @RequestParam(name = "sortDir") String sortDir,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model
    ) {

        if(sortDir == null || sortDir.isEmpty()) sortDir = "asc";

        CategoryPageInfo categoryPageInfo = new CategoryPageInfo();
        List<Category> listCategories =  categoryService.listByPage(categoryPageInfo, pageNum, sortDir, keyword);

        long startCount = (long) (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;

        if(endCount > categoryPageInfo.getTotalElements()) {
            endCount = categoryPageInfo.getTotalElements();
        }

        model.addAttribute("totalPages", categoryPageInfo.getTotalPages());
        model.addAttribute("totalItems", categoryPageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortField", "name");
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc" );
        model.addAttribute("moduleURL", "/categories");
        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> listCategories =  categoryService.listCategoriesUsedInForm();
        Category category = new Category();
        model.addAttribute("category", category);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");
        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile, Model model, RedirectAttributes redirectAttributes) throws IOException {
        redirectAttributes.addFlashAttribute("message", String.format("The category has been %s successfully", category.getId() == null ? "created" : "updated"));

            if(!multipartFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                category.setImage(fileName);

                Category savedCategory = categoryService.save(category);

                String uploadDir = "category-images/" + savedCategory.getId();
                SupabaseS3Util.removeFolder(uploadDir);
                SupabaseS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
            }
            else {
                if(category.getImage().isEmpty()) {
                    category.setImage("default.png");
                }
            }

            categoryService.save(category);
            return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.get(id);
            List<Category> listCategories =  categoryService.listCategoriesUsedInForm();
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", String.format("Edit Category (ID: %d)", id));
            model.addAttribute("listCategories", listCategories);
            return "categories/category_form";
        }catch (CategoryNotFoundException exception){
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String enableCategory(@PathVariable(name = "id") Integer id, @PathVariable("status") Boolean isEnabled, RedirectAttributes redirectAttributes) {
        categoryService.updateCategoryEnabledStatus(id, isEnabled);
        redirectAttributes.addFlashAttribute("message", String.format("The category with id %d has been %s",id, isEnabled ? "enabled" : "disabled"));
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id , RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            String uploadDir = "category-images/" + id;
            SupabaseS3Util.removeFolder(uploadDir);
            redirectAttributes.addFlashAttribute("message", String.format("The category ID %d has been deleted successfully", id));
        } catch (CategoryNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<Category> listCategories = categoryService.listAll();
        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.export(listCategories, response);
    }

}
