package com.shopme.admin.section;

import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.brand.Brand;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.section.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SectionController {
    private final SectionService sectionService;
    private final String defaultRedirectURL = "redirect:/sections/page/1?sortField=position&sortDir=asc";

    @Autowired
    public SectionController(SectionService sectionService, CategoryService categoryService) {
        this.sectionService = sectionService;
    }

    @GetMapping("/sections")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/sections/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model) {
        Page<Section> page =  sectionService.listByPage(pageNum);

        List<Section> listSections = page.getContent();

        long startCount = (long) (pageNum - 1) *  SectionService.SECTION_BY_PAGE + 1;
        long endCount = startCount +  SectionService.SECTION_BY_PAGE - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortField", "position");
        model.addAttribute("sortDir", "asc");
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listSections", listSections);
        model.addAttribute("moduleURL", "/sections");

        return "sections/sections";
    }

    @GetMapping("/sections/{id}/enabled/{status}")
    public String enableSection(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") Boolean status, RedirectAttributes ra) {
        try {
            sectionService.enableSection(id, status);
            ra.addFlashAttribute("message", String.format("The section with ID %d has been %s", id, status ? "enabled" : "disabled"));
        } catch (SectionNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/sections/{id}/position/{action}")
    public String updatePosition(@PathVariable(name = "id") Integer id, @PathVariable(name = "action") String action, RedirectAttributes ra) {
        try {
            sectionService.updatePosition(id, action);
            ra.addFlashAttribute("message", String.format("The section with ID %d has been moved %s by one position", id, action));
        } catch (SectionNotFoundException | InvalidSectionPosition e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/sections/new/{type}")
    public String createSection(@PathVariable(name = "type") String type, Model model) {
        SectionType sectionType = SectionType.valueOf(type.toUpperCase());
        Section section = new Section();
        section.setSectionType(sectionType);

        model.addAttribute("section", section);
        model.addAttribute("sectionType", type);
        model.addAttribute("sectionTitle", sectionType.defaultDescription());
        return "sections/section_form";
    }

    @GetMapping("/sections/edit/{id}")
    public String editSection(@PathVariable(name = "id") Integer id, RedirectAttributes ra, Model model) {
        try {
            Section section = sectionService.get(id);
            model.addAttribute("sectionType", section.getSectionType().toString().toLowerCase());
            model.addAttribute("section", section);
            return "sections/section_form";
        } catch (SectionNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/sections/delete/{id}")
    public String deleteSection(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
        try {
            sectionService.delete(id);
            ra.addFlashAttribute("message", String.format("The section with ID %d has been deleted", id));
        } catch (SectionNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultRedirectURL;
    }


    @PostMapping("/sections/save")
    public String saveSection(HttpServletRequest request, RedirectAttributes ra) {
        Section section = mapToSection(request);
        try {
            if(section.getId() != null) {
                ra.addFlashAttribute("message", String.format("The section with ID %d has been %s", section.getId(), "updated"));
            }
            else {
                ra.addFlashAttribute("message", "The section has been created");
            }
            sectionService.save(section);
        } catch (SectionNotFoundException e) {
          ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return defaultRedirectURL;
    }

    public Section mapToSection(HttpServletRequest request) {
        String sectionId = request.getParameter("id");
        String heading = request.getParameter("heading");
        String description = request.getParameter("description");
        SectionType sectionType = SectionType.valueOf(request.getParameter("sectionType"));
        boolean enabled = Boolean.parseBoolean(request.getParameter("enabled"));
        String[] sectionArticles = request.getParameterValues("sectionArticles");
        String[] sectionCategories = request.getParameterValues("sectionCategories");
        String[] sectionBrands = request.getParameterValues("sectionBrands");
        String[] sectionProducts = request.getParameterValues("sectionProducts");

        Section section = new Section();
        if(!sectionId.isEmpty()) {
            section.setId(Integer.parseInt(sectionId));
        }
        section.setHeading(heading);
        section.setDescription(description);
        section.setSectionType(sectionType);
        section.setEnabled(enabled);

        if(sectionArticles != null) {
            List<SectionArticle> sectionArticleList = new ArrayList<>();
            for(int i = 0; i < sectionArticles.length; i++) {
                SectionArticle sectionArticle = new SectionArticle();
                sectionArticle.setSection(section);

                int articleId = Integer.parseInt(sectionArticles[i]);
                sectionArticle.setArticle(new Article(articleId));

                sectionArticle.setPosition(i + 1);

                sectionArticleList.add(sectionArticle);
            }
            section.setSectionArticles(sectionArticleList);
        }

        if(sectionCategories != null) {
            List<SectionCategory> sectionCategoriesList = new ArrayList<>();
            for(int i = 0; i < sectionCategories.length; i++) {
                SectionCategory sectionCategory = new SectionCategory();
                sectionCategory.setSection(section);

                int categoryId = Integer.parseInt(sectionCategories[i]);
                sectionCategory.setCategory(new Category(categoryId));

                sectionCategory.setPosition(i + 1);

                sectionCategoriesList.add(sectionCategory);
            }
            section.setSectionCategories(sectionCategoriesList);
        }

        if(sectionBrands != null) {
            List<SectionBrand> sectionBrandsList = new ArrayList<>();
            for(int i = 0; i < sectionBrands.length; i++) {
                SectionBrand sectionBrand = new SectionBrand();
                sectionBrand.setSection(section);

                int brandId = Integer.parseInt(sectionBrands[i]);
                sectionBrand.setBrand(new Brand(brandId));

                sectionBrand.setPosition(i + 1);

                sectionBrandsList.add(sectionBrand);
            }
            section.setSectionBrands(sectionBrandsList);
        }

        if(sectionProducts != null) {
            List<SectionProduct> sectionProductsList = new ArrayList<>();
            for(int i = 0; i < sectionProducts.length; i++) {
                SectionProduct sectionProduct = new SectionProduct();
                sectionProduct.setSection(section);

                int productId = Integer.parseInt(sectionProducts[i]);
                sectionProduct.setProduct(new Product(productId));

                sectionProduct.setPosition(i + 1);

                sectionProductsList.add(sectionProduct);
            }
            section.setSectionProducts(sectionProductsList);
        }
        return section;
    }
}
