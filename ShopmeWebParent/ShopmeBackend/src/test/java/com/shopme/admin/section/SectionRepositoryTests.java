package com.shopme.admin.section;

import com.shopme.common.entity.brand.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.section.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class SectionRepositoryTests {
    private final SectionRepository repo;

    @Autowired
    public SectionRepositoryTests(SectionRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testSaveAllCategoriesSection() {
        Section section = new Section();
        section.setHeading("All categories");
        section.setSectionType(SectionType.ALL_CATEGORIES);
        section.setPosition(7);
        section.setEnabled(false);

        Section savedSection = repo.save(section);
        assertThat(savedSection.getId()).isGreaterThan(0);
    }

    @Test
    public void testSaveTextSection() {
        Section section = new Section();
        section.setHeading("Announcement");
        section.setSectionType(SectionType.TEXT);
        section.setPosition(1);
        section.setDescription("Dear Customers");
        section.setEnabled(false);

        Section savedSection = repo.save(section);
        assertThat(savedSection.getId()).isGreaterThan(0);
    }

    @Test
    public void testSaveProductSection() {
        Product product1 = new Product(59);
        Product product2 = new Product(32);

        Section section = new Section();
        section.setHeading("Best-Selling Products");
        section.setSectionType(SectionType.SELECTED_PRODUCTS);
        section.setPosition(2);
        section.setDescription("Most Recommended");
        section.setEnabled(false);

        List<Product> listProducts = List.of(product1, product2);

        List<SectionProduct> listSectionProducts = listProducts.stream().map(product -> {
            SectionProduct sectionProduct = new SectionProduct();
            sectionProduct.setSection(section);
            sectionProduct.setProduct(product);
            return sectionProduct;
        }).toList();

        section.setSectionProducts(listSectionProducts);

        Section savedSection = repo.save(section);

        assertThat(savedSection.getId()).isGreaterThan(0);
        assertThat(savedSection.getSectionProducts().size()).isGreaterThan(0);
    }

    @Test
    public void testSaveCategorySection() {
        Category category1 = new Category(7);
        Category category2 = new Category(12);
        Category category3 = new Category(20);

        Section section = new Section();
        section.setHeading("Featured Categories");
        section.setSectionType(SectionType.SELECTED_CATEGORIES);
        section.setPosition(2);
        section.setDescription("Many people are shopping in these categories the most");
        section.setEnabled(false);

        List<Category> listCategories = List.of(category1, category2, category3);

        List<SectionCategory> listSectionCategories = listCategories.stream().map(category -> {
            SectionCategory sectionCategory = new SectionCategory();
            sectionCategory.setSection(section);
            sectionCategory.setCategory(category);
            return sectionCategory;
        }).toList();

       section.setSectionCategories(listSectionCategories);

        Section savedSection = repo.save(section);

        assertThat(savedSection.getId()).isGreaterThan(0);
        assertThat(savedSection.getSectionCategories().size()).isGreaterThan(0);
    }

    @Test
    public void testSaveArticleSection() {
        Article article1 = new Article(4);
        Article article2 = new Article(5);
        Article article3 = new Article(13);
        Article article4 = new Article(16);

        Section section = new Section();
        section.setHeading("Shopping Tips");
        section.setSectionType(SectionType.SELECTED_ARTICLES);
        section.setPosition(2);
        section.setDescription("Recommended articles for shipping");
        section.setEnabled(false);

        List<Article> listArticles = List.of(article1, article2, article3, article4);

        List<SectionArticle> listSectionArticles = listArticles.stream().map(category -> {
            SectionArticle sectionArticle = new SectionArticle();
            sectionArticle.setSection(section);
            sectionArticle.setArticle(category);
            return sectionArticle;
        }).toList();

        section.setSectionArticles(listSectionArticles);

        Section savedSection = repo.save(section);

        assertThat(savedSection.getId()).isGreaterThan(0);
        assertThat(savedSection.getSectionArticles().size()).isGreaterThan(0);
    }

    @Test
    public void testSaveBrandSection() {
        Brand brand1 = new Brand(1);
        Brand brand2 = new Brand(2);
        Brand brand3 = new Brand(3);
        Brand brand4 = new Brand(4);

        Section section = new Section();
        section.setHeading("Feature Brands");
        section.setSectionType(SectionType.SELECTED_BRANDS);
        section.setPosition(2);
        section.setDescription("Featured Brands");
        section.setEnabled(false);

        List<Brand> listBrands = List.of(brand1, brand2, brand3, brand4);

        List<SectionBrand> listSectionBrands = listBrands.stream().map(category -> {
            SectionBrand sectionBrand = new SectionBrand();
            sectionBrand.setSection(section);
            sectionBrand.setBrand(category);
            return sectionBrand;
        }).toList();

        section.setSectionBrands(listSectionBrands);

        Section savedSection = repo.save(section);

        assertThat(savedSection.getId()).isGreaterThan(0);
        assertThat(savedSection.getSectionBrands().size()).isGreaterThan(0);
    }


}
