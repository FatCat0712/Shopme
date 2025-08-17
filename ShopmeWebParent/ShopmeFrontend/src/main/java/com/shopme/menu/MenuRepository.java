package com.shopme.menu;

import com.shopme.common.entity.menu.Menu;
import com.shopme.common.entity.menu.MenuType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    @Query("SELECT m FROM Menu m WHERE m.menuType = ?1 AND  m.enabled = true ORDER BY m.position")
    List<Menu> listMenusByMenuType(MenuType menuType);

    @Query("SELECT m FROM Menu m WHERE m.alias = ?1 AND  m.enabled = true")
    Menu findByAlias(String alias);
}
