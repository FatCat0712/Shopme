package com.shopme.article;

import com.shopme.common.entity.article.Article;
import com.shopme.common.exception.ArticleNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {
    private final ArticleRepository repo;

    @Autowired
    public ArticleService(ArticleRepository repo) {
        this.repo = repo;
    }

    public Article getByArticle(String alias) throws ArticleNotFound {
        Article article =  repo.findByAlias(alias);
        if(article == null) {
            throw new ArticleNotFound("Could not find any articles with " + alias);
        }
        return article;
    }
}
