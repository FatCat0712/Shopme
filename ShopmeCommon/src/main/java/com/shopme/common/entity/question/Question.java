package com.shopme.common.entity.question;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.IdBasedEntity;
import com.shopme.common.entity.User;
import com.shopme.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question extends IdBasedEntity {
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

    @Transient
    public boolean isAnswered() {
        return answerContent != null  &&  !answerContent.isEmpty();
    }

    @Column(nullable = false)
    private boolean approvalStatus;

    private int votes;

    public Question() {
    }

    public Question(int id) {
        this.id = id;
    }

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

    @Transient
    public String getFormattedAskTime() {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(this.askTime);
    }
}
