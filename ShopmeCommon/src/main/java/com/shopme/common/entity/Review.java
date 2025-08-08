package com.shopme.common.entity;

import com.shopme.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review extends IdBasedEntity{
    @Column(name = "headline", length = 150)
    private String headline;

    @Column(length = 500)
    private String comment;

    private int rating;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "review_time")
    private Date reviewTime;

    @Override
    public String toString() {
        return "Review{" +
                "headline='" + headline + '\'' +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", product=" + product.getId() +
                ", customer=" + customer.getId() +
                ", reviewTime=" + reviewTime +
                '}';
    }
}
