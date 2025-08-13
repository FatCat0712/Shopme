package com.shopme.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.question.Question;
import com.shopme.common.entity.vote.VoteResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class QuestionVoteRestControllerTests {
    private final QuestionRepository repo;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public QuestionVoteRestControllerTests(QuestionRepository repo, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.repo = repo;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testVoteLogin() throws Exception {
        String requestURL = "/vote_question/9/up";

        MvcResult mvcResult = mockMvc.perform(
                post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
        assertFalse(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("You must login");
    }

    @Test
    @WithMockUser(username = "tina.jamerson@gmail.com", password = "tina2020")
    public void testVoteNonExistQuestion() throws Exception {
        String requestURL = "/vote_question/123/up";

        MvcResult mvcResult = mockMvc.perform(
                post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
        assertFalse(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("no longer exists");
    }

    @Test
    @WithMockUser(username = "alex.stevenson@outlook.com", password = "alex2020")
    public void testVoteUp() throws Exception {
        Integer questionId = 9;
        String requestURL = "/vote_question/" + questionId + "/up";

        Optional<Question> question = repo.findById(questionId);
        assertTrue(question.isPresent());
        int voteCountBefore = question.get().getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("have voted up");
        int voteCountAfter = voteResult.getVoteCount();

        assertEquals(voteCountBefore + 1, voteCountAfter);
    }


    @Test
    @WithMockUser(username = "alex.stevenson@outlook.com", password = "alex2020")
    public void testUndoVoteUp() throws Exception {
        Integer questionId = 9;
        String requestURL = "/vote_question/" + questionId + "/up";

        Optional<Question> question = repo.findById(questionId);
        assertTrue(question.isPresent());
        int voteCountBefore = question.get().getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("have unvoted");
        int voteCountAfter = voteResult.getVoteCount();

        assertEquals(voteCountBefore - 1, voteCountAfter);
    }

    @Test
    @WithMockUser(username = "alex.stevenson@outlook.com", password = "alex2020")
    public void testVoteDown() throws Exception {
        Integer questionId = 9;
        String requestURL = "/vote_question/" + questionId + "/down";

        Optional<Question> question = repo.findById(questionId);
        assertTrue(question.isPresent());
        int voteCountBefore = question.get().getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("have voted down");
        int voteCountAfter = voteResult.getVoteCount();

        assertEquals(voteCountBefore - 1, voteCountAfter);
    }

    @Test
    @WithMockUser(username = "alex.stevenson@outlook.com", password = "alex2020")
    public void testUndoVoteDown() throws Exception {
        Integer questionId = 9;
        String requestURL = "/vote_question/" + questionId + "/down";

        Optional<Question> question = repo.findById(questionId);
        assertTrue(question.isPresent());
        int voteCountBefore = question.get().getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("have unvoted down");
        int voteCountAfter = voteResult.getVoteCount();

        assertEquals(voteCountBefore + 1, voteCountAfter);
    }
}
