package com.shopme.common.entity.article;

import com.shopme.common.entity.IdBasedEntity;
import com.shopme.common.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "articles")
@Getter
@Setter
public class Article extends IdBasedEntity {
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String alias;

    @Enumerated(EnumType.STRING)
    private ArticleType articleType;

    private Date updatedTime;

    private boolean published;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Article() {
    }

    public Article(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", alias='" + alias + '\'' +
                ", articleType=" + articleType +
                ", updatedTime=" + updatedTime +
                ", published=" + published +
                ", user=" + user +
                '}';
    }
}
