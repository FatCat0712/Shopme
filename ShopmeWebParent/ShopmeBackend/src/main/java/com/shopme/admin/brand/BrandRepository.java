package com.shopme.admin.brand;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.brand.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer>, SearchRepository<Brand, Integer> {
     Brand findByName(String name);
     Long countById(Integer id);

     @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
     Page<Brand> findAll(String keyword, Pageable Pageable);
}
