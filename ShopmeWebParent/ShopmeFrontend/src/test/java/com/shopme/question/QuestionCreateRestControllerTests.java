package com.shopme.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.question.ask.QuestionCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class QuestionCreateRestControllerTests {
    private final QuestionRepository repo;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public QuestionCreateRestControllerTests(QuestionRepository repo, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.repo = repo;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testCreateQuestionWithNoLogin() throws Exception {
        int productId = 59;
        String questionContent = "what the warranty of the product ?";
        QuestionCreateRequest request = new QuestionCreateRequest(questionContent, productId);
        String requestURL = "/questions/save";

        mockMvc.perform(post(requestURL)
                .with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    @Test
    @WithMockUser(username = "alex.stevenson@outlook.com", password = "alex2020")
    public void testCreateQuestionWithLogin() throws Exception {
        int productId = 59;
        String questionContent = "Is it good for children ?";
        QuestionCreateRequest request = new QuestionCreateRequest(questionContent, productId);
        String requestURL = "/questions/save";

        mockMvc.perform(post(requestURL)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @WithMockUser(username = "alex.stevenson@outlook.com", password = "alex2020")
    public void testCreateQuestionWithDuplicateContent() throws Exception {
        int productId = 59;
        String questionContent = "Is it good for children ?";
        QuestionCreateRequest request = new QuestionCreateRequest(questionContent, productId);
        String requestURL = "/questions/save";

        mockMvc.perform(post(requestURL)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andDo(print());

    }
}
