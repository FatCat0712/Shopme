package com.shopme.common.entity.review;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.IdBasedEntity;
import com.shopme.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review extends IdBasedEntity {
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    private int votes;

    @Transient
    private boolean upvotedByCurrentCustomer;

    @Transient
    private boolean downvotedByCurrentCustomer;


    public Review() {
    }

    public Review(int id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
