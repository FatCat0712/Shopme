package com.shopme.common.entity.product;

import com.shopme.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_images")
@Getter
@Setter
public class ProductImage extends IdBasedEntity {
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductImage() {
    }

    public ProductImage(Integer id, String name, Product product) {
        this.id = id;
        this.name = name;
        this.product = product;
    }

    public ProductImage(String imageName, Product product) {
        this.name = imageName;
        this.product = product;
    }


    @Transient
    public String getImagePath() {
        if(id == null) return "/images/image-thumbnail.png";
        return "/product-images/" + this.product.getId() + "/extras/" + name;
    }
}
