package com.shopme.question.ask;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionCreateResponse {
    private boolean successful;
    private String message;

    public QuestionCreateResponse(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
    }
}
