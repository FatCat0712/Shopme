package com.shopme.question.vote;

import com.shopme.common.entity.question.QuestionVote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionVoteRepository extends CrudRepository<QuestionVote, Integer> {
        @Query("SELECT qv FROM QuestionVote qv WHERE qv.question.id = ?1 AND qv.customer.id = ?2")
        QuestionVote findByQuestionAndCustomer(Integer questionId, Integer customerId);

        @Query("SELECT qv FROM QuestionVote qv WHERE qv.question.product.id = ?1 AND qv.customer.id = ?2")
        List<QuestionVote> findByProductAndCustomer(Integer productId, Integer customerId);



}
