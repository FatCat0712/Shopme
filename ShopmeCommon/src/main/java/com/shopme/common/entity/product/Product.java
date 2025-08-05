package com.shopme.common.entity.product;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Constants;
import com.shopme.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends IdBasedEntity {

    @Column(unique = true, length = 256, nullable = false)
    private String name;

    @Column(unique = true, length = 256, nullable = false)
    private String alias;

    @Column(length = 4096, nullable = false, name = "short_description")
    private String shortDescription;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false, name = "full_description")
    private String fullDescription;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    private boolean enabled;

   @Column(name = "in_stock")
    private boolean inStock;

    private float cost;
    private float price;

    @Column(name = "discount_percent")
    private float discountPercent;

    private float length;
    private float width;
    private float height;
    private float weight;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> details = new ArrayList<>();

    public Product() {
    }

    public Product(Integer id) {
        this.id = id;
    }

    public Product(String name) {
        this.name = name;
    }

    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    public void addDetail(String  name, String value) {
        ProductDetail productDetail = new ProductDetail(name, value, this);
        this.details.add(productDetail);
    }

    public void addDetail(int id, String name, String value) {
        ProductDetail productDetail = new ProductDetail(id, name, value, this);
        this.details.add(productDetail);
    }

    public boolean containsImageName(String name) {
        for (ProductImage productImage : images) {
            if (productImage.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortDescription='" + shortDescription +'\'' +
                '}';
    }

    @Transient
    public String getMainImagePath() {
        if(id == null || mainImage == null) return "/images/image-thumbnail.png";
        return Constants.SUPABASE_URI +  "/product-images/" + this.id + "/" + this.mainImage;
    }

    @Transient
    public String getShortName() {
        return name.length() > 70 ? name.substring(0, 70).concat("...") : name;
    }

    @Transient
    public float getDiscountPrice() {
      if(discountPercent > 0) {
          return (this.price * (100 - this.discountPercent))/ 100;
      }
      return this.price;
    }



}
