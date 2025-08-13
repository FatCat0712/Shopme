package com.shopme.admin.question;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer>, SearchRepository<Question, Integer> {
    @Query("SELECT q FROM Question q " +
            "WHERE q.product.name LIKE %?1% " +
            "OR CONCAT(q.asker.firstName,' ', q.asker.lastName) LIKE %?1% " +
            "OR q.questionContent LIKE %?1% " +
            "OR q.answerContent LIKE %?1%")
    Page<Question> findAll(String keyword, Pageable pageable);

    List<Question> findAll();
}
