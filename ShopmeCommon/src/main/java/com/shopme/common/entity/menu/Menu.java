package com.shopme.common.entity.menu;

import com.shopme.common.entity.IdBasedEntity;
import com.shopme.common.entity.article.Article;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "menus")
@Getter
@Setter
public class Menu extends IdBasedEntity {
    private String title;

    @OneToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Enumerated(EnumType.ORDINAL)
    private MenuType menuType;

    private String alias;

    private int position;

    private boolean enabled;

    @Override
    public String toString() {
        return "Menu{" +
                "title='" + title + '\'' +
                ", article=" + article.getId() +
                ", menuType=" + menuType +
                ", alias='" + alias + '\'' +
                ", position=" + position +
                ", enabled=" + enabled +
                '}';
    }
}
