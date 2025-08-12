package com.shopme.review.vote;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.review.Review;
import com.shopme.common.entity.review.ReviewVote;
import com.shopme.review.ReviewNotFoundException;
import com.shopme.review.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewVoteService {
    private final ReviewVoteRepository voteRepo;
    private final ReviewRepository reviewRepo;

    @Autowired
    public ReviewVoteService(ReviewVoteRepository voteRepo, ReviewRepository reviewRepo) {
        this.voteRepo = voteRepo;
        this.reviewRepo = reviewRepo;
    }

    public VoteResult undoVote(ReviewVote vote, Integer reviewId, VoteType voteType) {
        voteRepo.delete(vote);
        reviewRepo.updateVoteCount(reviewId);
        Integer voteCount = reviewRepo.getVoteCount(reviewId);
        return VoteResult.success("You have unvoted " + voteType + " that review.", voteCount);
    }

    public VoteResult doVote(Integer reviewId, Customer customer, VoteType voteType) throws ReviewNotFoundException {
        Optional<Review> review = reviewRepo.findById(reviewId);
        if(review.isEmpty()) {
            throw new ReviewNotFoundException("The review ID  " + reviewId + " no longer exists.");
        }
        Review savedReview = review.get();
        ReviewVote vote = voteRepo.findByReviewAndCustomer(savedReview.getId(), customer.getId());
        if(vote != null) {
            if(vote.isUpVoted() && voteType.equals(VoteType.UP) || vote.isDownVoted() && voteType.equals(VoteType.DOWN)) {
                return undoVote(vote, reviewId, voteType);
            }
            else if(vote.isUpVoted() && voteType.equals(VoteType.DOWN)) {
               vote.voteDown();
            }
            else if(vote.isDownVoted() && voteType.equals(VoteType.UP)) {
                vote.voteUp();
            }
        }
        else {
            vote = new ReviewVote();
            vote.setCustomer(customer);
            vote.setReview(savedReview);

            if(voteType.equals(VoteType.UP)) {
                vote.voteUp();
            }
            else {
                vote.voteDown();
            }
        }

        voteRepo.save(vote);
        reviewRepo.updateVoteCount(reviewId);
        Integer voteCount = reviewRepo.getVoteCount(reviewId);
        return VoteResult.success("You have voted " + voteType + " that review", voteCount);
    }

    public void markReviewsVotedProductByCustomer(List<Review> listReviews, Integer productId, Integer customerId) {
           List<ReviewVote> listVotes =  voteRepo.findByProductAndCustomer(productId, customerId);
           for(ReviewVote rv : listVotes) {
               Review votedReview = rv.getReview();
               if(listReviews.contains(votedReview)) {
                   int index = listReviews.indexOf(votedReview);
                   Review review = listReviews.get(index);
                   if(rv.isUpVoted()) {
                       review.setUpvotedByCurrentCustomer(true);
                   }
                   else if(rv.isDownVoted()){
                       review.setDownvotedByCurrentCustomer(true);
                   }
               }
           }
    }
}
