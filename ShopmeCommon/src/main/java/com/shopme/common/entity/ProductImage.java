package com.shopme.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Transient
    public String getImagePath() {
        if(id == null) return "/images/image-thumbnail.png";
        return "/product-images/" + this.product.getId() + "/extras/" + name;
    }
}
