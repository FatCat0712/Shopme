package com.shopme.product;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.Category_;
import com.shopme.common.entity.brand.Brand;
import com.shopme.common.entity.brand.Brand_;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.product.Product_;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecs {
    public static Specification<Product> matchBrandList(List<String> brands) {
        return ((root, query, criteriaBuilder) -> {
            Join<Product, Brand> brandJoin = root.join(Product_.BRAND);
            return brandJoin.get(Brand_.NAME).in(brands);
        });
    }

    public static Specification<Product> matchCategoryList(List<String> categories) {
        return ((root, query, criteriaBuilder) ->   {
            Join<Product, Category> categoryJoin = root.join(Product_.CATEGORY);
            return categoryJoin.get(Category_.ALIAS).in(categories);
        });
    }

    public static Specification<Product> isInStock() {
        return ((root, query, criteriaBuilder) ->  criteriaBuilder.isTrue(root.get(Product_.inStock)));
    }

    public static Specification<Product> isOnSale() {
      return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(Product_.DISCOUNT_PERCENT), 0);
    }

    public static Specification<Product> matchRating(Integer rating) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(Product_.AVERAGE_RATING), rating);
    }
}
