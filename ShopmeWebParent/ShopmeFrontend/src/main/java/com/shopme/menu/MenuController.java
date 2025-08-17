package com.shopme.menu;

import com.shopme.common.entity.menu.Menu;
import com.shopme.common.exception.MenuNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MenuController {
    private final MenuService service;

    @Autowired
    public MenuController(MenuService service) {
        this.service = service;
    }

    @GetMapping("/m/{alias}")
    public String getByAlias(@PathVariable(name = "alias") String alias, Model model) {
        try {
            Menu menu = service.findByAlias(alias);
            model.addAttribute("menu", menu);
            return "menu/menu_detail";
        } catch (MenuNotFoundException e) {
            return "error/404";
        }
    }
}
