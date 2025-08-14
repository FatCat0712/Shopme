package com.shopme.common.entity.review;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.VoteBasedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "review_votes")
@Getter
@Setter
public class ReviewVote extends VoteBasedEntity {
    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Override
    public String toString() {
        return "ReviewVote{" +
                "review=" + review.getId() +
                ", customer=" + customer.getId() +
                '}';
    }
}
