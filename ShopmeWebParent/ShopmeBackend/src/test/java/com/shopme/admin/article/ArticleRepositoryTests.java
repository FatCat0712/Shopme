package com.shopme.admin.article;

import com.shopme.common.entity.User;
import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.article.ArticleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ArticleRepositoryTests {
    private final ArticleRepository repo;

    @Autowired
    public ArticleRepositoryTests(ArticleRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testCreateArticle() {
        int userId = 1;
        Article article = new Article();
        article.setTitle("Payments – Safe, Fast, and Convenient");
        article.setAlias("payments");
        article.setContent("Shopme offers a variety of secure payment methods including credit cards, e-wallets, and bank transfers. All transactions are encrypted to protect your financial information. We also support cash-on-delivery for select locations.");
        article.setArticleType(ArticleType.MENU);
        article.setPublished(true);
        article.setUser(new User(userId));
        article.setUpdatedTime(new Date());
        repo.save(article);
    }

    @Test
    public void testGetArticle() {
        int articleId = 1;
        Optional<Article> article = repo.findById(articleId);
        assertThat(article.isPresent()).isTrue();
    }

    @Test
    public void testListArticles() {
        List<Article> listArticles = (List<Article>)repo.findAll();
        assertThat(listArticles.size()).isGreaterThan(1);
    }

    @Test
    public void testUpdateArticle() {
        String content = "nothing here";
        int articleId = 1;
        Optional<Article> article = repo.findById(articleId);
        assertTrue(article.isPresent());
        Article articleInDB = article.get();
        articleInDB.setContent(content);
        Article updatedArticle = repo.save(articleInDB);
        assertThat(updatedArticle.getContent()).isEqualTo(content);
    }

    @Test
    public void testDeleteArticle() {
        int articleId = 1;
        Optional<Article> article = repo.findById(articleId);
        assertTrue(article.isPresent());
        repo.deleteById(articleId);
        Optional<Article> deletedArticle = repo.findById(articleId);
        assertThat(deletedArticle).isEmpty();

    }





}
