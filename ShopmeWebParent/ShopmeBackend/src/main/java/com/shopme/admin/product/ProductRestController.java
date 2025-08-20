package com.shopme.admin.product;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductRestController {
    private final ProductService productService;

    @Autowired
    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products/check_name")
    public String checkUniqueName(@RequestParam(name = "id", required = false) Integer id, @RequestParam(name = "name") String name) {
        return productService.checkName(id, name);
    }

    @GetMapping("/products/get/{id}")
    public ProductDTO getProductInfo(@PathVariable(name = "id") Integer id) throws ProductNotFoundException {
        Product product =  productService.get(id);
        return new ProductDTO(product.getName(), product.getShortName(), product.getMainImagePath(), product.getDiscountPrice(), product.getCost());
    }


}
