package com.shopme.admin.menu;

import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.menu.Menu;
import com.shopme.common.entity.menu.MenuType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class MenuRepositoryTests {
    private final MenuRepository repo;

    @Autowired
    public MenuRepositoryTests(MenuRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testSaveMenu() {
        int articleId = 14;
        Menu menu = new Menu();
        menu.setTitle("Contact Us");
        menu.setAlias(menu.getTitle().replace(" ", "-").toLowerCase());
        menu.setMenuType(MenuType.HEADER);
        menu.setArticle(new Article(articleId));
        menu.setEnabled(false);
        menu.setPosition(4);
        Menu savedMenu = repo.save(menu);
        assertThat(savedMenu.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetMenu() {
        int menuId = 1;
        Optional<Menu> menu = repo.findById(menuId);
        assertThat(menu.isPresent()).isTrue();
    }

    @Test
    public void testListMenus() {
        List<Menu> menus = repo.findAll();
        assertThat(menus.size()).isGreaterThan(0);
        menus.forEach(System.out::println);
    }

    @Test
    public void updateMenu() {
        int menuId = 2;
        String alias = "careers";
        Optional<Menu> menu = repo.findById(menuId);
        assertTrue(menu.isPresent());
        Menu menuInDB = menu.get();
        menuInDB.setAlias(alias);
        Menu updatedMenu = repo.save(menuInDB);
        assertThat(updatedMenu.getAlias()).isEqualTo(alias);
    }

    @Test
    public  void deleteMenu() {
        int menuId = 2;
        repo.deleteById(menuId);
        Optional<Menu> menu = repo.findById(menuId);
        assertThat(menu.isEmpty()).isTrue();
    }
}
