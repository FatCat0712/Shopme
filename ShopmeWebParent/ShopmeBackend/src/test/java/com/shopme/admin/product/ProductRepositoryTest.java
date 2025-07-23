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

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

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

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);

    }

    @Test
    public void testListAllProducts() {
       Iterable<Product> iterableProducts =  productRepository.findAll();
       iterableProducts.forEach( System.out::println);
    }

    @Test
    public void testGetProduct() {
        Integer id = 2;
        Optional<Product> productById = productRepository.findById(id);
        assertThat(productById.isPresent()).isTrue();
        System.out.println(productById.get());
    }

    @Test
    public void testUpdateProduct() {
        Integer id = 1;
        Optional<Product> productById = productRepository.findById(id);
        productById.ifPresent(product -> product.setPrice(499));

        productRepository.save(productById.get());

        Product updatedProduct = entityManager.find(Product.class, id);

        assertThat(updatedProduct.getPrice()).isEqualTo(499);
    }

    @Test
    public void testDeleteById() {
        Integer id = 3;
        productRepository.deleteById(id);

       Optional<Product> result = productRepository.findById(id);

       assertThat(result.isEmpty()).isTrue();

    }

    @Test
    public void testSaveProductWithImages() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();

        assertThat(product.getImages().size()).isEqualTo(6);
    }

    @Test
    public void testSaveProductWithDetails() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();
        product.addDetail("Device Memory", "128 GB");
        product.addDetail("CPU Model", "MediaTek");
        product.addDetail("OS", "Android 10");

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getDetails()).isNotEmpty();
    }
}
