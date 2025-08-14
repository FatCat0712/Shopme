package com.shopme.question.ask;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQuestionResponse {
    private boolean successful;
    private String message;

    public CreateQuestionResponse(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
    }
}
