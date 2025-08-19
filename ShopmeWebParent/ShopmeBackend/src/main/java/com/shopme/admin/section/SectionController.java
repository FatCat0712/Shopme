package com.shopme.admin.section;

import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.brand.Brand;
import com.shopme.common.entity.section.*;
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

        SectionDTO section = new SectionDTO();
        section.setSectionType(sectionType);

        model.addAttribute("section", section);
        model.addAttribute("sectionType", type);
        model.addAttribute("sectionTitle", sectionType.defaultDescription());
        return "sections/section_form";
    }

    @PostMapping("/sections/save")
    public String saveSection(SectionDTO sectionDTO, RedirectAttributes ra) {
        Section section = mapToSection(sectionDTO);
        try {
            sectionService.save(section);
            if(section.getId() != null) {
                ra.addFlashAttribute("message", String.format("The section with ID %d has been %s", section.getId(), "updated"));
            }
            else {
                ra.addFlashAttribute("message", "The section has been created");
            }
        } catch (SectionNotFoundException e) {
          ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return defaultRedirectURL;
    }

    public Section mapToSection(SectionDTO dto) {
        Section section = new Section();
        Integer sectionId = dto.getId();
        if(sectionId != null) {
            section.setId(sectionId);
        }
        section.setHeading(dto.getHeading());
        section.setDescription(dto.getDescription());
        section.setSectionType(dto.getSectionType());
        section.setEnabled(dto.isEnabled());

        if(dto.getSectionArticles() != null) {
            List<Integer> sectionArticlesByNum = dto.getSectionArticles();
            List<SectionArticle> sectionArticleList = new ArrayList<>();
            for(int i = 1; i <= sectionArticlesByNum.size(); i++) {
                SectionArticle sectionArticle = new SectionArticle();
                sectionArticle.setSection(section);
                sectionArticle.setArticle(new Article(sectionArticlesByNum.get(i  - 1)));
                sectionArticle.setPosition(i);
                sectionArticleList.add(sectionArticle);
            }
            section.setSectionArticles(sectionArticleList);
        }

        if(dto.getSectionBrands() != null) {
            List<Integer> sectionBrandsByNum = dto.getSectionBrands();
            List<SectionBrand> sectionBrandList = new ArrayList<>();
            for(int i = 1; i <= sectionBrandsByNum.size(); i++) {
                SectionBrand sectionBrand = new SectionBrand();
                sectionBrand.setSection(section);
                sectionBrand.setBrand(new Brand(sectionBrandsByNum.get(i  - 1)));
                sectionBrand.setPosition(i);
                sectionBrandList.add(sectionBrand);
            }
            section.setSectionBrands(sectionBrandList);
        }


        if(dto.getSectionCategories() != null) {
            List<Integer> sectionCategoryByNum = dto.getSectionCategories();
            List<SectionCategory> sectionCategoryList = new ArrayList<>();
            for(int i = 1; i <= sectionCategoryByNum.size(); i++) {
                SectionCategory sectionCategory = new SectionCategory();
                sectionCategory.setSection(section);
                sectionCategory.setCategory(new Category(sectionCategoryByNum.get(i  - 1)));
                sectionCategory.setPosition(i);
                sectionCategoryList.add(sectionCategory);
            }
            section.setSectionCategories(sectionCategoryList);
        }
        return section;
    }
}
