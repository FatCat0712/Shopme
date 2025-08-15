package com.shopme.article;

import com.shopme.common.entity.article.Article;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Integer> {
    @Query("SELECT a FROM Article a WHERE a.alias = ?1")
    Article findByAlias(String alias);
}
