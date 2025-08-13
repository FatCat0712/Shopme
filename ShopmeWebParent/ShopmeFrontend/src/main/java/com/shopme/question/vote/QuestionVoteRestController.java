package com.shopme.question.vote;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.vote.VoteResult;
import com.shopme.common.entity.vote.VoteType;
import com.shopme.question.QuestionNotFound;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionVoteRestController {
    private final QuestionVoteService service;
    private final ControllerHelper controllerHelper;

    @Autowired
    public QuestionVoteRestController(QuestionVoteService service, ControllerHelper controllerHelper) {
        this.service = service;
        this.controllerHelper = controllerHelper;
    }

    @PostMapping("/vote_question/{id}/{type}")
    public VoteResult voteQuestion(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "type") String type,
            HttpServletRequest request
    ) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        if(customer == null) {
            return VoteResult.fail("You must login to vote the question");
        }

        VoteType voteType = VoteType.valueOf(type.toUpperCase());
        try {
          return service.doVote(id, customer, voteType);
        } catch (QuestionNotFound e) {
            return VoteResult.fail(e.getMessage());
        }
    }
}
