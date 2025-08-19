package com.shopme.common.entity.section;

import com.shopme.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
@Getter
@Setter
public class Section extends IdBasedEntity {
    private String heading;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private SectionType sectionType;

    private int position;

    private boolean enabled;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SectionProduct> sectionProducts = new ArrayList<>();

    @OneToMany(mappedBy =  "section", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SectionCategory> sectionCategories = new ArrayList<>();

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SectionBrand> sectionBrands = new ArrayList<>();

    @OneToMany(mappedBy =  "section", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SectionArticle> sectionArticles = new ArrayList<>();

    public Section() {
    }

    public Section(String heading, String description, SectionType sectionType, boolean enabled) {
        this.heading = heading;
        this.description = description;
        this.sectionType = sectionType;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Section{" +
                "heading='" + heading + '\'' +
                ", description='" + description + '\'' +
                ", sectionType=" + sectionType +
                ", position=" + position +
                ", enabled=" + enabled +
                ", sectionProducts=" + sectionProducts.size() +
                ", sectionCategories=" + sectionCategories.size() +
                ", sectionBrands=" + sectionBrands.size() +
                ", sectionArticles=" + sectionArticles.size() +
                '}';
    }
}
