package com.shopme.brand;

import com.shopme.common.entity.brand.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Brand findByName(String name);

    @Query("SELECT b FROM Brand b INNER JOIN b.categories c WHERE c.alias IN ?1")
    Page<Brand> findByCategory(List<String> categoryAlias, Pageable pageable);

    @Query("SELECT b FROM Brand b JOIN b.categories c WHERE c.alias = ?1")
    List<Brand> findByCategory(String alias);
}

