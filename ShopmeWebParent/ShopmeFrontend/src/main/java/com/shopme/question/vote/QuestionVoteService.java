package com.shopme.question.vote;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.question.Question;
import com.shopme.common.entity.question.QuestionVote;
import com.shopme.common.entity.vote.VoteResult;
import com.shopme.common.entity.vote.VoteType;
import com.shopme.question.QuestionNotFound;
import com.shopme.question.QuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class QuestionVoteService {
    private final QuestionVoteRepository  voteRepo;
    private final QuestionRepository questionRepo;

    @Autowired
    public QuestionVoteService(QuestionVoteRepository voteRepo, QuestionRepository questionRepo) {
        this.voteRepo = voteRepo;
        this.questionRepo = questionRepo;
    }

    public VoteResult undoVote(QuestionVote vote, Integer questionId, VoteType voteType) {
        voteRepo.delete(vote);
        questionRepo.updateVoteCount(questionId);
        Integer voteCount = questionRepo.getVoteCount(questionId);
        return VoteResult.success("You have unvoted " + voteType + " that question.", voteCount);
    }

    public VoteResult doVote(Integer questionId, Customer customer, VoteType voteType) throws QuestionNotFound {
        int customerId = customer.getId();
        Optional<Question> question = questionRepo.findById(questionId);
        if(question.isEmpty()) {
            throw new QuestionNotFound("The question with ID " + questionId + " is no longer exists");
        }

        Question savedQuestion = question.get();
        QuestionVote vote = voteRepo.findByQuestionAndCustomer(questionId, customerId);
        if(vote != null) {
            if(vote.isUpVoted() && voteType.equals(VoteType.UP) || vote.isDownVoted() && voteType.equals(VoteType.DOWN)) {
                return undoVote(vote, questionId, voteType);
            }
            else if(vote.isUpVoted() && voteType.equals(VoteType.DOWN)) {
                vote.voteDown();
            }
            else if(vote.isDownVoted() && voteType.equals(VoteType.UP))  {
                vote.voteUp();
            }
        }else {
            vote = new QuestionVote();
            vote.setQuestion(savedQuestion);
            vote.setCustomer(customer);
            if(voteType.equals(VoteType.UP)) {
                vote.voteUp();
            }
            else if(voteType.equals(VoteType.DOWN)) {
                vote.voteDown();
            }
        }

        voteRepo.save(vote);
        questionRepo.updateVoteCount(questionId);
        Integer voteCount = questionRepo.getVoteCount(questionId);
        return VoteResult.success("You have voted " + voteType + " that review", voteCount);
    }

}
