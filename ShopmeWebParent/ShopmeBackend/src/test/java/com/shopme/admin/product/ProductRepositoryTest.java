package com.shopme.admin.product;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTest {
    private final ProductRepository repo;
    private final TestEntityManager entityManager;

    @Autowired
    public ProductRepositoryTest(ProductRepository repo, TestEntityManager entityManager) {
        this.repo = repo;
        this.entityManager = entityManager;
    }

    @Test
    public void testCreateProduct() {
        Brand brand = entityManager.find(Brand.class, 37);
        Category category = entityManager.find(Category.class, 5);

        Product product = new Product();
        product.setName("Acer Aspire Desktop");
        product.setAlias("acer_aspire_desktop");
        product.setShortDescription("Short description for Acer Aspire");
        product.setFullDescription("Full description for Acer Aspire");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(678);
        product.setCost(600);

        product.setEnabled(true);
        product.setInStock(true);

        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = repo.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);

    }

    @Test
    public void testListAllProducts() {
       Iterable<Product> iterableProducts =  repo.findAll();
       iterableProducts.forEach( System.out::println);
    }

    @Test
    public void testGetProduct() {
        Integer id = 2;
        Optional<Product> productById = repo.findById(id);
        assertThat(productById.isPresent()).isTrue();
        System.out.println(productById.get());
    }

    @Test
    public void testUpdateProduct() {
        Integer id = 1;
        Optional<Product> productById = repo.findById(id);
        productById.ifPresent(product -> product.setPrice(499));
        assertTrue(productById.isPresent());
        repo.save(productById.get());

        Product updatedProduct = entityManager.find(Product.class, id);

        assertThat(updatedProduct.getPrice()).isEqualTo(499);
    }

    @Test
    public void testDeleteById() {
        Integer id = 3;
        repo.deleteById(id);

       Optional<Product> result = repo.findById(id);

       assertThat(result.isEmpty()).isTrue();

    }

    @Test
    public void testSaveProductWithImages() {
        Integer productId = 1;
        Optional<Product> product = repo.findById(productId);
        assertTrue(product.isPresent());
        Product savedProduct = product.get();

        assertThat(savedProduct.getImages().size()).isEqualTo(6);
    }

    @Test
    public void testSaveProductWithDetails() {
        Integer productId = 1;
        Optional<Product> product = repo.findById(productId);
        assertTrue(product.isPresent());
        Product savedProduct = product.get();

        savedProduct.addDetail("Device Memory", "128 GB");
        savedProduct.addDetail("CPU Model", "MediaTek");
        savedProduct.addDetail("OS", "Android 10");

        Product updatedProduct = repo.save(savedProduct);
        assertThat(updatedProduct.getDetails()).isNotEmpty();
    }

    @Test
    public void testUpdateReviewCountAndAverageRating() {
        Integer productId = 100;
        repo.updateReviewCountAndAverageRating(productId);
    }
}
