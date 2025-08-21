package com.shopme.admin.article;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.User;
import com.shopme.common.entity.article.Article;
import com.shopme.common.entity.article.ArticleType;
import com.shopme.common.exception.ArticleNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArticleService {
    private final ArticleRepository repo;
    private final static int ARTICLE_PER_PAGE = 5;

    @Autowired
    public ArticleService(ArticleRepository repo) {
        this.repo = repo;
    }

    public List<Article> listAll() {
        return repo.findAll();
    }

    public void listByPage(PagingAndSortingHelper helper, int pageNum) {
        helper.listEntities(pageNum, ARTICLE_PER_PAGE, repo);
    }

    public Article getByAlias(String alias) throws ArticleNotFoundException {
        Article article = repo.findByAlias(alias);
        if(article == null) {
            throw new ArticleNotFoundException("Could not find any articles with alias " + alias);
        }
        return article;
    }

    public Article getById(Integer articleId) throws ArticleNotFoundException {
        Optional<Article> article = repo.findById(articleId);
        if(article.isEmpty()) {
            throw new ArticleNotFoundException("Could not find any articles with ID " + articleId);
        }
        return article.get();
    }

    public void publishArticle(Integer articleId, Boolean status) throws ArticleNotFoundException {
        Optional<Article> article = repo.findById(articleId);
        if(article.isEmpty()) {
            throw new ArticleNotFoundException("Could not find any articles with ID " + articleId);
        }
        repo.publishArticle(articleId, status);
    }

    public void saveArticle(Article articleInForm, User publisher) throws ArticleNotFoundException {
         Integer articleId = articleInForm.getId();
        Article resultArticle;
        int userId = publisher.getId();
        if(articleId != null) {
            Optional<Article> article = repo.findById(articleId);
            if(article.isEmpty()) {
                throw new ArticleNotFoundException("Could not find any articles with ID " + articleId);
            }
            else {
                resultArticle = article.get();
                resultArticle.setTitle(articleInForm.getTitle());
                resultArticle.setContent(articleInForm.getContent());
                resultArticle.setArticleType(articleInForm.getArticleType());
                resultArticle.setPublished(articleInForm.isPublished());
                resultArticle.setAlias(articleInForm.getAlias());
            }
        }
        else {
            resultArticle = articleInForm;
        }

        if(resultArticle.getAlias().isEmpty()) {
            String formattedAlias = resultArticle.getTitle().replaceAll(" & ", "-").replaceAll(" ", "-").toLowerCase();
            resultArticle.setAlias(formattedAlias);
        }
        resultArticle.setUser(new User(userId));
        resultArticle.setUpdatedTime(new Date());
        repo.save(resultArticle);
    }

    public void deleteArticle(Integer articleId) throws ArticleNotFoundException {
        Optional<Article> article = repo.findById(articleId);
        if(article.isEmpty()) {
            throw new ArticleNotFoundException("Could not find any articles with ID " + articleId);
        }
        repo.deleteById(articleId);
    }

    public List<Article> listUnlinkedArticlesByArticleType(ArticleType articleType) {
        return repo.listUnlinkedArticleByArticleType(articleType);
    }

    public Integer countMenuBoundArticles() {
        return listAll().stream().filter(a -> a.getArticleType().equals(ArticleType.MENU)).toList().size();
    }

    public Integer countFreeArticles() {
        return listAll().stream().filter(a -> a.getArticleType().equals(ArticleType.FREE)).toList().size();
    }

    public Integer countPublishedArticles() {
        return listAll().stream().filter(Article::isPublished).toList().size();
    }

    public Integer countUnPublishedArticles() {
        return listAll().stream().filter(a -> !a.isPublished()).toList().size();
    }
}
