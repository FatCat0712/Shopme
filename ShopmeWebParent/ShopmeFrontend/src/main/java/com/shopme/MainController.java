package com.shopme;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.section.Section;
import com.shopme.section.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class MainController {
    private final SectionService sectionService;
    private final CategoryService categoryService;

    @Autowired
    public MainController(SectionService sectionService, CategoryService categoryService) {
        this.sectionService = sectionService;
        this.categoryService = categoryService;
    }

    @RequestMapping(value = {"/","", "/home"})
    public String viewHomePage(Model model) {
        List<Section> listSections = sectionService.listAll();
        List<Category> listCategories = categoryService.listNoChildrenCategories();
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("listSections", listSections);
        return "index";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        else {
            return "redirect:/home";
        }
    }


}
