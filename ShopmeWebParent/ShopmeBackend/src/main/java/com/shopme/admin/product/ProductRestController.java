package com.shopme.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {
    @Autowired
    private ProductService productService;

    @PostMapping("/products/check_name")
    public String checkUniqueName(@RequestParam(name = "id", required = false) Integer id, @RequestParam(name = "name") String name) {
        return productService.checkName(id, name);
    }
}
