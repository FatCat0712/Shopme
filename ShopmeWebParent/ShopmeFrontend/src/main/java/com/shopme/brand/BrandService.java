package com.shopme.brand;

import com.shopme.common.entity.brand.Brand;
import com.shopme.common.exception.BrandNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrandService {
    private final BrandRepository repo;

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
}
