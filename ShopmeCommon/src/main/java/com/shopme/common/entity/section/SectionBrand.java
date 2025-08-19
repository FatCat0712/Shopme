package com.shopme.common.entity.section;

import com.shopme.common.entity.brand.Brand;
import com.shopme.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "section_brands")
@Getter
@Setter
public class SectionBrand extends IdBasedEntity {
    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private int position;
}
