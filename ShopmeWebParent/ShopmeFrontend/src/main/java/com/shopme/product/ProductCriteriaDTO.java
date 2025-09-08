package com.shopme.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCriteriaDTO {
    private String brand;
    private String category;
    private String inStock;
    private String onSales;
    private String rating;
}
