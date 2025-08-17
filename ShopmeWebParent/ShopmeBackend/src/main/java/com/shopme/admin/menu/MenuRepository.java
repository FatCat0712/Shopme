package com.shopme.admin.menu;

import com.shopme.common.entity.menu.Menu;
import com.shopme.common.entity.menu.MenuType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    @Query("UPDATE Menu m SET m.enabled = ?2 WHERE m.id = ?1")
    @Modifying
    void updateMenuStatus(Integer menuId, Boolean status);

    @Query("SELECT m FROM Menu m WHERE m.menuType = ?1 AND m.position = ?2")
    Menu findByMenuTypeAndPosition(MenuType menuType, Integer position);

    @Query("SELECT m FROM Menu m WHERE m.menuType = ?1")
    List<Menu> listByMenuType(MenuType menuType);


}
