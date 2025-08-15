package com.shopme.admin.article;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.User;
import com.shopme.common.entity.article.Article;
import com.shopme.common.exception.ArticleNotFound;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class ArticleService {
    private final ArticleRepository repo;
    private final int ARTICLE_PER_PAGE = 5;

    @Autowired
    public ArticleService(ArticleRepository repo) {
        this.repo = repo;
    }

    public void listByPage(PagingAndSortingHelper helper, int pageNum) {
        helper.listEntities(pageNum, ARTICLE_PER_PAGE, repo);
    }

    public Article getByAlias(String alias) throws ArticleNotFound {
        Article article = repo.findByAlias(alias);
        if(article == null) {
            throw new ArticleNotFound("Could not find any articles with alias " + alias);
        }
        return article;
    }

    public Article getById(Integer articleId) throws ArticleNotFound {
        Optional<Article> article = repo.findById(articleId);
        if(article.isEmpty()) {
            throw new ArticleNotFound("Could not find any articles with ID " + articleId);
        }
        return article.get();
    }

    public void publishArticle(Integer articleId, Boolean status) throws ArticleNotFound {
        Optional<Article> article = repo.findById(articleId);
        if(article.isEmpty()) {
            throw new ArticleNotFound("Could not find any articles with ID " + articleId);
        }
        repo.publishArticle(articleId, status);
    }

    public void saveArticle(Article articleInForm, User publisher) throws ArticleNotFound {
         Integer articleId = articleInForm.getId();
        Article resultArticle;
        int userId = publisher.getId();
        if(articleId != null) {
            Optional<Article> article = repo.findById(articleId);
            if(article.isEmpty()) {
                throw new ArticleNotFound("Could not find any articles with ID " + articleId);
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

        if(resultArticle.getAlias() == null) {
            String formattedAlias = resultArticle.getTitle().replace(" ", "-");
            resultArticle.setAlias(formattedAlias);
        }
        resultArticle.setUser(new User(userId));
        resultArticle.setUpdatedTime(new Date());
        repo.save(resultArticle);
    }

    public void deleteArticle(Integer articleId) throws ArticleNotFound {
        Optional<Article> article = repo.findById(articleId);
        if(article.isEmpty()) {
            throw new ArticleNotFound("Could not find any articles with ID " + articleId);
        }
        repo.deleteById(articleId);
    }
}
