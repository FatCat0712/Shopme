package com.shopme.common.entity.section;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "section_categories")
@Getter
@Setter
public class SectionCategory extends IdBasedEntity {
    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private int position;
}
