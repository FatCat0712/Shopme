package com.shopme.admin.article;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.article.ArticleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Integer>, SearchRepository<Article, Integer> {
    @Query("SELECT a FROM Article a WHERE a.title LIKE %?1% OR a.content LIKE %?1%")
    Page<Article> findAll(String keyword, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.alias = ?1")
    Article findByAlias(String alias);

    @Query("UPDATE Article a SET a.published = ?2 WHERE a.id = ?1")
    @Modifying
    void publishArticle(Integer articleId, Boolean status);

    @Query("SELECT a FROM Article a WHERE a.articleType = ?1 AND a.id NOT IN (SELECT m.article.id FROM Menu m WHERE m.article IS NOT NULL)")
    List<Article> listUnlinkedArticleByArticleType(ArticleType articleType);

    @Query("SELECT a FROM Article a WHERE a.articleType = ?1")
    List<Article> listAllArticleByArticleType(ArticleType articleType);


}
