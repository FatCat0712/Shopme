package com.shopme.common.entity.review;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "review_votes")
@Getter
@Setter
public class ReviewVote extends IdBasedEntity {
    private static final int VOTE_UP_POINT = 1;
    private static final int VOTE_DOWN_POINT = -1;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    private int votes;

    public void voteUp() {
        this.votes = VOTE_UP_POINT;
    }

    public void voteDown() {
        this.votes = VOTE_DOWN_POINT;
    }


    @Override
    public String toString() {
        return "ReviewVote{" +
                "customer=" + customer.getId() +
                ", review=" + review.getId() +
                ", votes=" + votes +
                '}';
    }

    @Transient
    public boolean isUpVoted() {
        return this.votes == VOTE_UP_POINT;
    }

    @Transient
    public boolean isDownVoted() {
        return this.votes == VOTE_DOWN_POINT;
    }
}
