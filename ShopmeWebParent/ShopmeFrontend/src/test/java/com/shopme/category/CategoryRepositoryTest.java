package com.shopme.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTest {
   @Autowired
   private CategoryRepository categoryRepository;

   @Test
    public void testListEnabledCategories() {
        List<Category> categories = categoryRepository.findAllEnabled();
        categories.forEach(category -> System.out.println(category.getName() + " (" + category.isEnabled() + ") "));
    }

    @Test
    public void testFindCategoryByAlias() {
       String alias = "electronics";
       Category category = categoryRepository.findByAliasEnabled(alias);
       assertThat(category).isNotNull();
    }


}
