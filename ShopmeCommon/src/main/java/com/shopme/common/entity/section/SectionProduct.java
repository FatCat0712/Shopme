package com.shopme.common.entity.section;

import com.shopme.common.entity.IdBasedEntity;
import com.shopme.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "section_products")
@Getter
@Setter
public class SectionProduct extends IdBasedEntity {
    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int position;
}
