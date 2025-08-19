package com.shopme.admin.brand;

import com.shopme.common.entity.brand.Brand;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTest {
    @Autowired
    private BrandRepository brandRepository;

    @Test
    public void createBrands() {
        Brand brand1 = new Brand(1, "Acer", "logo-png", Set.of(new Category(6)));
        Brand brand2 = new Brand(2, "Apple", "logo-png", Set.of(new Category(4), new Category(7)));
        Brand brand3 = new Brand(3, "Samsung", "logo-png", Set.of(new Category(29), new Category(24)));

        Brand acer = brandRepository.save(brand1);
        Brand apple = brandRepository.save(brand2);
        Brand samsung = brandRepository.save(brand3);

        assertThat(acer.getId()).isGreaterThan(0);
        assertThat(apple.getId()).isGreaterThan(0);
        assertThat(samsung.getId()).isGreaterThan(0);
    }


    @Test
    public void findAllBrands() {
        List<Brand> brands = brandRepository.findAll();

        brands.forEach(System.out::println);
    }

    @Test
    public void getById() {
        Optional<Brand> result = brandRepository.findById(1);
        assertThat(result.get().getId()).isGreaterThan(0);
    }

    @Test
    public void updateBrand() {
        Brand brand = brandRepository.findById(3).get();
        brand.setName("Samsung Electronics");
        Brand updatedBrand = brandRepository.save(brand);

        assertThat(updatedBrand.getName()).isEqualTo("Samsung Electronics");
    }

    @Test
    public void deleteBrand(){
        Brand brand = brandRepository.findByName("Apple");
        if(brand != null) {
            brandRepository.deleteById(brand.getId());
        }
        Brand deletedBrand = brandRepository.findByName("Apple");
        assertThat(deletedBrand).isNull();
    }
}
