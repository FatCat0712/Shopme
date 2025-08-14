package com.shopme.question.ask;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionCreateRequest {
    private String questionContent;
    private Integer productId;

    public QuestionCreateRequest() {
    }

    public QuestionCreateRequest(String questionContent, Integer productId) {
        this.questionContent = questionContent;
        this.productId = productId;
    }
}
