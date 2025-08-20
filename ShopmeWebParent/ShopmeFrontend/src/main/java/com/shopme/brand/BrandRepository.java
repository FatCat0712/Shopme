package com.shopme.brand;

import com.shopme.common.entity.brand.Brand;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends CrudRepository<Brand, Integer> {
    Brand findByName(String name);
}
