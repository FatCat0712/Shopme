package com.shopme.admin.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private String name;
    private String shortName;
    private String imagePath;
    private float price;
    private float cost;

    public ProductDTO(String name, String shortName, String imagePath, float price, float cost) {
        this.name = name;
        this.shortName = shortName;
        this.imagePath = imagePath;
        this.price = price;
        this.cost = cost;
    }
}
