package com.shopme.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.review.Review;
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
public class ReviewVoteRestControllerTests {
    private final ReviewRepository reviewRepo;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReviewVoteRestControllerTests(ReviewRepository reviewRepo, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.reviewRepo = reviewRepo;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testVoteNotLogin() throws Exception {
        String requestURL = "/vote_review/1/up";

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
    public void testVoteNonExistReview() throws Exception {
        String requestURL = "/vote_review/123/up";

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
    @WithMockUser(username = "tina.jamerson@gmail.com", password = "tina2020")
    public void testVoteUp() throws Exception {
        Integer reviewId = 10;
        String requestURL = "/vote_review/" + reviewId + "/up";

        Optional<Review> review = reviewRepo.findById(reviewId);
        assertTrue(review.isPresent());
        int voteCountBefore = review.get().getVotes();

        MvcResult mvcResult = mockMvc.perform(
                        post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("have voted");
        int voteCountAfter = voteResult.getVoteCount();

        assertEquals(voteCountBefore + 1, voteCountAfter);
    }


    @Test
    @WithMockUser(username = "tina.jamerson@gmail.com", password = "tina2020")
    public void testUndoVoteUp() throws Exception {
        Integer reviewId = 10;
        String requestURL = "/vote_review/" + reviewId + "/up";

        Optional<Review> review = reviewRepo.findById(reviewId);
        assertTrue(review.isPresent());
        int voteCountBefore = review.get().getVotes();

        MvcResult mvcResult = mockMvc.perform(
                        post(requestURL).with(csrf()))
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
    @WithMockUser(username = "tina.jamerson@gmail.com", password = "tina2020")
    public void testVoteDown() throws Exception {
        Integer reviewId = 4;
        String requestURL = "/vote_review/" + reviewId + "/down";

        Optional<Review> review = reviewRepo.findById(reviewId);
        assertTrue(review.isPresent());
        int voteCountBefore = review.get().getVotes();

        MvcResult mvcResult = mockMvc.perform(
                        post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("have voted");
        int voteCountAfter = voteResult.getVoteCount();

        assertEquals(voteCountBefore - 1, voteCountAfter);
    }

    @Test
    @WithMockUser(username = "tina.jamerson@gmail.com", password = "tina2020")
    public void testUndoVoteDown() throws Exception {
        Integer reviewId = 4;
        String requestURL = "/vote_review/" + reviewId + "/down";

        Optional<Review> review = reviewRepo.findById(reviewId);
        assertTrue(review.isPresent());
        int voteCountBefore = review.get().getVotes();

        MvcResult mvcResult = mockMvc.perform(
                        post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("have unvoted");
        int voteCountAfter = voteResult.getVoteCount();

        assertEquals(voteCountBefore + 1, voteCountAfter);
    }




}
