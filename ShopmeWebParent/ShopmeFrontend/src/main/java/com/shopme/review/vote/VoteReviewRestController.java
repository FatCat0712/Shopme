package com.shopme.review.vote;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.vote.VoteResult;
import com.shopme.common.entity.vote.VoteType;
import com.shopme.review.ReviewNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoteReviewRestController {
    private final ReviewVoteService voteService;
    private final ControllerHelper controllerHelper;

    @Autowired
    public VoteReviewRestController(ReviewVoteService voteService, ControllerHelper controllerHelper) {
        this.voteService = voteService;
        this.controllerHelper = controllerHelper;
    }

    @PostMapping("/vote_review/{id}/{type}")
    public VoteResult voteReview(
            @PathVariable(name = "id") Integer reviewId,
            @PathVariable(name = "type") String type,
            HttpServletRequest request
    ) {
            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            if(customer == null) {
                return VoteResult.fail("You must login to vote the review");
            }
            VoteType voteType = VoteType.valueOf(type.toUpperCase());
        try {
           return voteService.doVote(reviewId, customer, voteType);
        } catch (ReviewNotFoundException e) {
           return VoteResult.fail(e.getMessage());
        }
    }
}
