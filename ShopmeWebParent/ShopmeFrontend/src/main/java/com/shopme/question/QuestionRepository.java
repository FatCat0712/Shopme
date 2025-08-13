package com.shopme.question;

import com.shopme.common.entity.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("SELECT q FROM Question q " +
            "WHERE q.approvalStatus = true AND " +
            "q.product.id = ?1")
    Page<Question> findByProduct(Integer productId, Pageable pageable);

    @Query("SELECT COUNT(q.id) FROM Question q  " +
            "WHERE q.answerContent IS NOT NULL " +
            "AND LENGTH(q.answerContent) > 0  "+
            "AND q.product.id = ?1")
    Integer countAnsweredQuestionByProduct(Integer productId);

    @Query("UPDATE Question q " +
            "SET q.votes = (SELECT COALESCE(SUM(qv.votes),0) FROM QuestionVote qv WHERE qv.question.id = ?1) " +
            "WHERE q.id = ?1")
    @Modifying
    void updateVoteCount(Integer questionId);

    @Query("SELECT q.votes FROM Question q WHERE q.id = ?1")
    Integer getVoteCount(Integer questionId);
}
