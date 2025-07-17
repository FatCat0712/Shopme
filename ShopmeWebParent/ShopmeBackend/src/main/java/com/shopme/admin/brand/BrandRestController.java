package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class BrandRestController {
    @Autowired
    private BrandService brandService;

    @PostMapping("/brands/check_name")
    public String checkUnique(@RequestParam(name = "id", required = false) Integer id,@RequestParam(name="name") String name) {
        return brandService.checkUnique(id, name);
    }

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundRestException {
        try {
            Brand brand = brandService.get(brandId);
            Set<Category> categories = brand.getCategories();
            return categories.stream().map(category -> new CategoryDTO(category.getId(), category.getName())).collect(Collectors.toList());
        } catch (BrandNotFound e) {
           throw new BrandNotFoundRestException();
        }

    }
}
