package com.shopme.brand;

import com.shopme.common.entity.brand.Brand;
import com.shopme.common.exception.BrandNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BrandService {
    private final BrandRepository repo;
    public static final int BRAND_PER_PAGE = 12;

    @Autowired
    public BrandService(BrandRepository repo) {
        this.repo = repo;
    }

    public Brand findByName(String brandName) throws BrandNotFoundException {
        Brand brand =  repo.findByName(brandName);
        if(brand == null) {
            throw new BrandNotFoundException("Could not find any brand with the name " + brandName);
        }
        return brand;
    }

    public Page<Brand> listBrandsByCategory(String categoryAliasList, Integer pageNum) {
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(pageNum - 1, BRAND_PER_PAGE, sort);
        Page<Brand> page;
        if(categoryAliasList != null) {
            List<String> categoryAlias =Arrays.stream(categoryAliasList.split(",")).toList();
            page = repo.findByCategory(categoryAlias, pageable);
        }
        else {
            page = repo.findAll(pageable);
        }
        return page;
    }

    public List<Brand> listAll() {
        return repo.findAll();
    }



}
