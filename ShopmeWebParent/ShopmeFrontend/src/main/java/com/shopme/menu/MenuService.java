package com.shopme.menu;

import com.shopme.common.entity.menu.Menu;
import com.shopme.common.entity.menu.MenuType;
import com.shopme.common.exception.MenuNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    private final MenuRepository repo;

    @Autowired
    public MenuService(MenuRepository repo) {
        this.repo = repo;
    }

    public List<Menu> listMenusByMenuType(MenuType menuType) {
        return repo.listMenusByMenuType(menuType);
    }

    public Menu findByAlias(String alias) throws MenuNotFoundException {
       Menu menu =  repo.findByAlias(alias);
       if(menu == null) {
           throw new MenuNotFoundException("Could not find any menus with alias " + alias);
       }
       return menu;
    }
}
