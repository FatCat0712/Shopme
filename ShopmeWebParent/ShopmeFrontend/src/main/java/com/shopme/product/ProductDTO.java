package com.shopme.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private String mainImage;
    private String alias;
    private String shortName;
    private String brandName;
    private float price;
    private float discountPercent;
    private float discountPrice;


    public ProductDTO() {
    }

    public ProductDTO(String mainImage, String alias, String shortName, String brandName, float price, float discountPercent, float discountPrice) {
        this.mainImage = mainImage;
        this.alias = alias;
        this.shortName = shortName;
        this.brandName = brandName;
        this.price = price;
        this.discountPercent = discountPercent;
        this.discountPrice = discountPrice;
    }
}
