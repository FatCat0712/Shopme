package com.shopme.admin.brand;

import com.shopme.admin.SupabaseS3Util;
import com.shopme.admin.brand.export.BrandCsvExporter;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.brand.Brand;
import com.shopme.common.exception.BrandNotFoundException;
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
public class BrandController {
    private final BrandService brandService;
    private final CategoryService categoryService;

    private final String defaultURL =  "redirect:/brands/page/1?sortField=name&sortDir=asc";

    @Autowired
    public BrandController(BrandService brandService, CategoryService categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("/brands")
    public String getBrands() {
        return defaultURL;
    }

    @GetMapping("/brands/page/{pageNum}")
    public String getBrandsByPage(@PagingAndSortingParam(listName = "listBrands", moduleURL = "/brands") PagingAndSortingHelper helper,
                                  @PathVariable(name = "pageNum") Integer pageNum
                                  ){

       brandService.listByPage(pageNum, helper);

        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String createNewBrand(Model model) {
        List<Category> sortedCategories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("pageTitle", "Create New Brand");
        model.addAttribute("brand", new Brand());
        model.addAttribute("listCategories", sortedCategories);
        return "brands/brand_form";
    }

    @PostMapping("/brands/save")
    public String saveBrand(
            Brand brand,
            @RequestParam("fileImage") MultipartFile multipartFile,
            RedirectAttributes redirectAttributes
    ) throws IOException {
        redirectAttributes.addFlashAttribute("message", String.format("The brand has been %s successfully", brand.getId() == null ? "created" : "updated"));
         if(!multipartFile.isEmpty()) {
             String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
             brand.setLogo(fileName);

             Brand savedBrand = brandService.save(brand);

             String uploadDir = "brand-logos/" + savedBrand.getId();

             SupabaseS3Util.removeFolder(uploadDir);
             SupabaseS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
         }
         else {
             if(brand.getLogo().isEmpty()) {
                 brand.setLogo("logo.png");
             }
         }

         brandService.save(brand);

         return defaultURL;
    }


    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            Brand savedBrand = brandService.get(id);
            List<Category> sortedCategories = categoryService.listCategoriesUsedInForm();
            model.addAttribute("brand", savedBrand);
            model.addAttribute("pageTitle", String.format("Edit Brand (ID: %d)", savedBrand.getId()));
            model.addAttribute("listCategories", sortedCategories);
            return "brands/brand_form";
        }catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return defaultURL;
        }
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
             brandService.delete(id);
             String brandDir = "brand-logos/" + id;
            SupabaseS3Util.removeFolder(brandDir);
            redirectAttributes.addFlashAttribute("message", String.format("The brand with id %d has been deleted successfully", id));
            return defaultURL;
        }catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return defaultURL;
        }
    }

    @GetMapping("/brands/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Brand> listBrands = brandService.listAll();
        System.out.println(listBrands);
        BrandCsvExporter exporter = new BrandCsvExporter();
        exporter.export(listBrands, response);
    }




}
