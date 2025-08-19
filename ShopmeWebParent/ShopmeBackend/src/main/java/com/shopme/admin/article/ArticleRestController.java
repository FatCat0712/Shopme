package com.shopme.admin.article;

import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.article.ArticleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArticleRestController {
    private final ArticleService service;

    @Autowired
    public ArticleRestController(ArticleService service) {
        this.service = service;
    }

    @GetMapping("/articles/list_articles")
    public List<ArticleDTO> listArticles() {
        List<Article> listArticles = service.listAll();
        return listArticles.stream().map(article -> new ArticleDTO(article.getId(), article.getTitle())).toList();
    }
}
