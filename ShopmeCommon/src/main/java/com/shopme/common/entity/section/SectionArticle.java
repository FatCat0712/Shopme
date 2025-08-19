package com.shopme.common.entity.section;

import com.shopme.common.entity.IdBasedEntity;
import com.shopme.common.entity.article.Article;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "section_articles")
@Getter
@Setter
public class SectionArticle extends IdBasedEntity {
    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    public SectionArticle() {
    }

    public SectionArticle(Section section, Article article, int position) {
        this.section = section;
        this.article = article;
        this.position = position;
    }

    private int position;
}
