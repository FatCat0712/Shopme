package com.shopme.product;

import com.shopme.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>, CrudRepository<Product, Integer> {
    @Query("SELECT p FROM Product p " +
            "WHERE p.enabled = true AND p.category.id = ?1 " +
            "OR p.category.allParentIDs LIKE %?2% ORDER BY p.name")
    Page<Product> listByCategory(Integer categoryId, String categoryMatch, Pageable pageable);

    public Product findByAlias(String alias);

    @Query(value = "SELECT * FROM products p WHERE p.enabled = true AND " +
            "MATCH(name, short_description, full_description) AGAINST(?1)", nativeQuery = true)
    public Page<Product> search(String keyword, Pageable pageable);
}
