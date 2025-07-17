package com.shopme.admin.brand;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends CrudRepository<Brand, Integer>, SearchRepository<Brand, Integer> {
     Brand findByName(String name);
     Long countById(Integer id);

     @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
     Page<Brand> findAll(String keyword, Pageable Pageable);

     @Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER BY b.name ASC")
     public List<Brand> findAll();

}
