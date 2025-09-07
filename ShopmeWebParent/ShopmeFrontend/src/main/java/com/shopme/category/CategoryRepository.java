package com.shopme.category;

import com.shopme.common.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.name ASC")
    List<Category> findAllEnabled();

    List<Category> findTop8ByEnabledTrueOrderByIdAsc();

    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.id > 8 ORDER BY c.name ASC")
    List<Category> listRemainingCategories();


    @Query("SELECT c FROM Category  c WHERE c.enabled = true AND c.alias = ?1")
    Category findByAliasEnabled(String alias);


}
