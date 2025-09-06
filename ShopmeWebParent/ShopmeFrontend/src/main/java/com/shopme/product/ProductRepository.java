package com.shopme.product;

import com.shopme.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>, CrudRepository<Product, Integer> {
    @Query("SELECT p FROM Product p " +
            "WHERE p.enabled = true AND p.category.id = ?1 " +
            "OR p.category.allParentIDs LIKE %?2% ORDER BY p.name")
    Page<Product> listByCategory(Integer categoryId, String categoryMatch, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.enabled = true AND p.id != ?3 AND p.category.id = ?1 " +
            "OR p.category.allParentIDs LIKE %?2% ORDER BY p.name LIMIT 5")
    List<Product> listByCategory(Integer categoryId, String categoryMatch, Integer currentProductId);

    @Query("SELECT p FROM Product p " +
            "WHERE p.enabled = true AND p.brand.id = ?1")
    Page<Product> listByBrand(Integer brandId, Pageable pageable);

     Product findByAlias(String alias);

    @Query(value = "SELECT * FROM products p WHERE p.enabled = true AND " +
            "MATCH(name, short_description, full_description) AGAINST(?1)", nativeQuery = true)
     Page<Product> search(String keyword, Pageable pageable);

    @Query("UPDATE Product p " +
            "SET p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id = ?1), " +
            "p.averageRating = (SELECT COALESCE(AVG(r.rating),0) FROM Review r WHERE r.product.id = ?1) " +
            "WHERE p.id = ?1")
    @Modifying
    void updateReviewCountAndAverageRating(Integer productId);

}
