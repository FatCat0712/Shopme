package com.shopme.admin.menu;

import com.shopme.admin.article.ArticleService;
import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.article.ArticleType;
import com.shopme.common.entity.menu.Menu;
import com.shopme.common.exception.MenuNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MenuController {
    private final MenuService menuService;
    private final ArticleService articleService;
    private final String defaultRedirectURL = "redirect:/menus";

    @Autowired
    public MenuController(MenuService menuService, ArticleService articleService) {
        this.menuService = menuService;
        this.articleService = articleService;
    }

    @GetMapping("/menus")
    public String listAll(Model model) {
        List<Menu> listMenus = menuService.listAll();
        model.addAttribute("listMenus", listMenus);
        return "menus/menus";
    }

    @GetMapping("/menus/new")
    public String createMenu(Model model) {
        Menu menu = new Menu();
        List<Article> listArticles = articleService.listUnlinkedArticlesByArticleType(ArticleType.MENU);
        model.addAttribute("pageTitle", "Create New Menu Item");
        model.addAttribute("listArticles", listArticles);
        model.addAttribute("menu", menu);
        return "menus/menu_form";
    }

    @PostMapping("/menus/save")
    public String saveMenu(Menu menuInForm,RedirectAttributes ra) {
        try {
            Integer menuId = menuInForm.getId();
            menuService.saveMenu(menuInForm);
            ra.addFlashAttribute("message", String.format("The %s menu has been %s", menuInForm.getTitle(), menuId != null ? "updated" : "created"));
        } catch (MenuNotFoundException e) {
            ra.addFlashAttribute("errorMessage", "Could not update as: " + e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/menus/edit/{id}")
    public String editMenu(@PathVariable(name = "id") Integer id, RedirectAttributes ra, Model model){
        try {
            Menu menu = menuService.get(id);
            List<Article> listArticles = articleService.listUnlinkedArticlesByArticleType(ArticleType.MENU);
            model.addAttribute("listArticles", listArticles);
            model.addAttribute("menu", menu);
            model.addAttribute("pageTitle", String.format("Edit %s Menu", menu.getTitle()));
            return "menus/menu_form";
        } catch (MenuNotFoundException e) {
           ra.addFlashAttribute("errorMessage", e.getMessage());
           return defaultRedirectURL;
        }
    }

    @GetMapping("/menus/{id}/enabled/{status}")
    public String enableMenu(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") Boolean status, RedirectAttributes ra) {
        try {
            Menu updatedMenu = menuService.enableMenu(id, status);
            ra.addFlashAttribute("message", String.format("Menu %s has been %s", updatedMenu.getTitle(), status ? "enabled" : "disabled"));
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
       return defaultRedirectURL;
    }

    @GetMapping("/menus/{id}/position/{action}")
    public String updatePosition(@PathVariable(name = "id") Integer id, @PathVariable(name = "action") String action, RedirectAttributes ra) {
        try {
            Menu menu = menuService.updateMenuPosition(id, action);
            ra.addFlashAttribute("message", String.format("The %s menu has been moved %s by one position", menu.getTitle(), action));
        } catch (MenuNotFoundException | InvalidMenuPosition e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/menus/delete/{id}")
    public String deleteMenu(@PathVariable(name = "id") Integer menuId, RedirectAttributes ra) {
        try {
            Menu menu = menuService.deleteMenu(menuId);
            ra.addFlashAttribute("message", String.format("The %s menu has been deleted", menu.getTitle()));
        } catch (MenuNotFoundException e) {
           ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultRedirectURL;
    }
}
