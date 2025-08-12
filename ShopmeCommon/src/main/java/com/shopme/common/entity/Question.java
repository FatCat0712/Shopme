package com.shopme.common.entity;

import com.shopme.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question extends IdBasedEntity{
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer asker;

    @Column(nullable = false, length = 1000)
    private String questionContent;

    @Column(nullable = false)
    private Date askTime;

    @Column(columnDefinition = "TEXT")
    private String answerContent;

    @ManyToOne
    @JoinColumn(name = "answered_by")
    private User answerer;

    private Date answerTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus;

    private int votes;

    @Override
    public String toString() {
        return "Question{" +
                "product=" + product.getId() +
                ", asker=" + asker.getId() +
                ", questionContent='" + questionContent + '\'' +
                ", askTime=" + askTime +
                ", answerContent='" + answerContent + '\'' +
                ", answerTime=" + answerTime +
                ", approvalStatus=" + approvalStatus +
                ", votes=" + votes +
                '}';
    }
}
