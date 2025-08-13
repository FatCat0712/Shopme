package com.shopme.common.entity.question;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.vote.VoteBasedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "question_votes")
@Getter
@Setter
public class QuestionVote extends VoteBasedEntity {
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Override
    public String toString() {
        return "QuestionVote{" +
                "question=" + question.getId() +
                ", customer=" + customer.getId() +
                '}';
    }
}
