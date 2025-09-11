package com.shopme.admin.menu;

import com.shopme.common.entity.menu.Menu;
import com.shopme.common.entity.menu.MenuType;
import com.shopme.common.exception.MenuNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MenuService {
    private final MenuRepository repo;

    @Autowired
    public MenuService(MenuRepository repo) {
        this.repo = repo;
    }

    public List<Menu> listAll(){
        Sort sort = Sort.by("menuType").ascending().and(Sort.by("position").ascending());
        return repo.findAll(sort);
    }

    public Menu get(Integer menuId) throws MenuNotFoundException {
        Optional<Menu> menu = repo.findById(menuId);
        if(menu.isEmpty()) {
            throw new MenuNotFoundException("Could not find any menus with ID " + menuId);
        }
        return menu.get();
    }

    public Menu enableMenu(Integer menuId, Boolean status) throws MenuNotFoundException {
        Menu menu = get(menuId); repo.updateMenuStatus(menuId, status);
        return menu;
    }

    public Menu updateMenuPosition(Integer menuId, String action) throws MenuNotFoundException, InvalidMenuPosition {
//        get the menu from database first
        Menu menu = get(menuId);
        MenuType menuType = menu.getMenuType();
        int oldPosition = menu.getPosition();

        List<Menu> listMenusWithSameType = repo.listByMenuType(menu.getMenuType());
        int firstPosition = 1;
        int lastPosition = listMenusWithSameType.size();

        int newPosition = 0;
        if(action.equals("up")) {
            newPosition = oldPosition - 1;
            if(newPosition < firstPosition) {
                throw new InvalidMenuPosition(String.format("The %s menu is already at the first position", menu.getTitle()));
            }
        }
        else if(action.equals("down")) {
            newPosition = oldPosition + 1;
            if(newPosition >lastPosition) {
                throw new InvalidMenuPosition(String.format("The %s menu is already at the last position", menu.getTitle()));
            }
        }

//        check if there is with the same menuType and updated position exists
        Menu samePositionMenu = repo.findByMenuTypeAndPosition(menuType, newPosition);
        if(samePositionMenu != null) {
            samePositionMenu.setPosition(oldPosition);
            menu.setPosition(newPosition);
            repo.save(samePositionMenu);
        }
        else {
            menu.setPosition(newPosition);
        }

        return repo.save(menu);
    }

    public void saveMenu(Menu menuInForm) throws MenuNotFoundException {
        Integer menuId = menuInForm.getId();
        Menu menu;
        if(menuId != null) {
            try {
                menu = get(menuId);
                menu.setEnabled(menuInForm.isEnabled());
                MenuType oldMenuType = menu.getMenuType();
                MenuType inFormMenuType = menuInForm.getMenuType();
                if(!oldMenuType.equals(inFormMenuType)) {
                    List<Menu> listMenusWithSameType = repo.listByMenuType(menuInForm.getMenuType());
                    int oldPosition = menu.getPosition();
                    int newPosition = listMenusWithSameType.size() + 1;
                    menu.setPosition(newPosition);
                    menu.setMenuType(inFormMenuType);
                    repo.updatePositionOfRemainingMenusWithSameType(oldMenuType, oldPosition);
                }
            } catch (MenuNotFoundException e) {
                throw new MenuNotFoundException(e.getMessage());
            }
        }
        else {
            menu = new Menu();
            List<Menu> listMenusWithSameType = repo.listByMenuType(menuInForm.getMenuType());
            int newPosition = listMenusWithSameType.size() + 1;
            menu.setPosition(newPosition);
            menu.setEnabled(menuInForm.isEnabled());
            menu.setMenuType(menuInForm.getMenuType());
        }

        menu.setTitle(menuInForm.getTitle());
        menu.setArticle(menuInForm.getArticle());
        String alias = menuInForm.getAlias();
        if(alias.isEmpty()) {
            alias = menuInForm.getTitle().replaceAll(" & ", "-").replaceAll(" ", "-").toLowerCase();;
        }
        menu.setAlias(alias);
        repo.save(menu);
    }



    public Menu deleteMenu(Integer menuId) throws MenuNotFoundException {
        try {
            Menu menu = get(menuId);
            int oldPosition = menu.getPosition();
            MenuType oldMenuType = menu.getMenuType();
            repo.deleteById(menu.getId());
            repo.updatePositionOfRemainingMenusWithSameType(oldMenuType, oldPosition);
            return menu;
        } catch (MenuNotFoundException e) {
            throw new MenuNotFoundException(e.getMessage());
        }
    }

    public Integer countHeaderMenus() {
        return listAll().stream().filter(m -> m.getMenuType().equals(MenuType.HEADER)).toList().size();
    }

    public Integer countFooterMenus() {
        return listAll().stream().filter(m -> m.getMenuType().equals(MenuType.FOOTER)).toList().size();
    }

    public Integer countEnabledMenus() {
        return listAll().stream().filter(Menu::isEnabled).toList().size();
    }

    public Integer countDisabledMenus() {
        return listAll().stream().filter(m -> !m.isEnabled()).toList().size();
    }
}
