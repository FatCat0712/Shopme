package com.shopme.question.ask;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQuestionRequest {
    private String questionContent;
    private Integer productId;

    public CreateQuestionRequest(String questionContent, Integer productId) {
        this.questionContent = questionContent;
        this.productId = productId;
    }
}
