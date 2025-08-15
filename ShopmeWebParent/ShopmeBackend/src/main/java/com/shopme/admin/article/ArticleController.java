package com.shopme.admin.article;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.User;
import com.shopme.common.entity.article.Article;
import com.shopme.common.exception.ArticleNotFound;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;
    private final String defaultURL = "redirect:/articles/page/1?sortField=title&sortDir=asc";

    @Autowired
    public ArticleController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping("/articles")
    public String listFirstPage() {
        return defaultURL;
    }

    @GetMapping("/articles/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(moduleURL = "/articles", listName = "listArticles")PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") Integer pageNum
    ) {
       articleService.listByPage(helper, pageNum);
       return "articles/articles";
    }

    @GetMapping("/articles/new")
    public String createArticle(Model model) {
        Article article = new Article();
        model.addAttribute("article", article);
        model.addAttribute("pageTitle", "Create New Article");
        return "articles/article_form";
    }

    @PostMapping("/articles/save")
    public String saveArticle(Article article, Authentication authentication, RedirectAttributes ra) {
        String email = authentication.getName();
       Integer articleId = article.getId();
        User currentUser = userService.getByEmail(email);
        try {
            if(articleId != null) {
                ra.addFlashAttribute("message", String.format("The article with ID %d has been updated", articleId));
            }
            else {
                ra.addFlashAttribute("message", "The article has been created");
            }
            articleService.saveArticle(article, currentUser);
        } catch (ArticleNotFound e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultURL;
    }

    @GetMapping("/articles/a/{article_alias}")
    public String viewArticleDetail(@PathVariable(name = "article_alias") String articleAlias, Model model, RedirectAttributes ra) {
        try {
            Article article = articleService.getByAlias(articleAlias);
            String cleanedContent =  Jsoup.clean(article.getContent(), Safelist.relaxed());
            article.setContent(cleanedContent);
            model.addAttribute("article", article);
            model.addAttribute("pageTitle", article.getTitle());
            return "articles/article_detail_modal";
        } catch (ArticleNotFound e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return defaultURL;
        }
    }

    @GetMapping("/articles/{id}/publish/{status}")
    public String publishArticle(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "status") Boolean status,
            RedirectAttributes ra
    ) {
        try {
            articleService.publishArticle(id, status);
            ra.addFlashAttribute("message", String.format("The article with ID %d has been %s", id, status ? "published" : "unpublished"));
        } catch (ArticleNotFound e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultURL;
    }

    @GetMapping("/articles/edit/{id}")
    public String editArticle(@PathVariable(name = "id") Integer id, RedirectAttributes ra, Model model) {
        try {
            Article article = articleService.getById(id);
            model.addAttribute("article", article);
            model.addAttribute("pageTitle", String.format("Edit Article (ID:%d)", id));
            return "articles/article_form";
        } catch (ArticleNotFound e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return defaultURL;
        }
    }

    @GetMapping("/articles/delete/{id}")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
        try {
            articleService.deleteArticle(id);
            ra.addFlashAttribute("message", String.format("The article with ID %d has been deleted", id));
        } catch (ArticleNotFound e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultURL;
    }




}
