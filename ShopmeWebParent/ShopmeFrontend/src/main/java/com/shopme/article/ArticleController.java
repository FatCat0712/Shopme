package com.shopme.article;

import com.shopme.common.entity.article.Article;
import com.shopme.common.exception.ArticleNotFound;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ArticleController {
    private final ArticleService service;

    @Autowired
    public ArticleController(ArticleService service) {
        this.service = service;
    }

    @GetMapping("/articles/a/{alias}")
    public String getByAlias(@PathVariable(name = "alias") String alias, Model model) {
        try {
           Article article = service.getByArticle(alias);
           String cleanContent = Jsoup.clean(article.getContent(), Safelist.relaxed());
           article.setContent(cleanContent);
           model.addAttribute("article", article);
           return "article/article_detail";
        } catch (ArticleNotFound e) {
            return "error/404";
        }
    }
}
