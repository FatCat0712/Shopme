package com.shopme.admin.brand;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.brand.Brand;
import com.shopme.common.exception.BrandNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public static final int BRAND_PER_PAGE = 10;

    public List<Brand> listAll() {
        return brandRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, BRAND_PER_PAGE, brandRepository );

    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand get(Integer id) throws BrandNotFoundException {
        Optional<Brand> brandById =  brandRepository.findById(id);
        if(brandById.isEmpty()) {
            throw new BrandNotFoundException("Could not find brand with id " + id);
        }
        return brandById.get();
    }

    public void delete(Integer id) throws BrandNotFoundException {
        Long countById = brandRepository.countById(id);
        if(countById == null || countById == 0) {
            throw new BrandNotFoundException("Could not find brand with id " + id);
        }
        else {
            brandRepository.deleteById(id);
        }
    }

    public String checkUnique(Integer id, String name) {
        Brand brand = brandRepository.findByName(name);

        // Case 1: No one uses the email → it's unique
        if(brand == null) return "OK";

        // Case 2: Creating new user, but email already exists → not unique
        boolean isCreatingNew = (id == null);

        if(isCreatingNew) {
           return "Duplicate";
        }

        // Case 3: Updating an existing user
        // If the email belongs to *another* user, then not unique
        return brand.getId().equals(id) ? "OK" : "Duplicate";

    }
}
