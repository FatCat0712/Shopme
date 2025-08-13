package com.shopme.question;

import com.shopme.common.entity.question.Question;
import com.shopme.common.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    private final QuestionRepository repo;
    public static final int QUESTION_PER_PAGE = 10;

    @Autowired
    public QuestionService(QuestionRepository repo) {
        this.repo = repo;
    }

    public Page<Question> listByProduct(Product product,int pageNum, String sortField,String sortDir) {
        int productId = product.getId();
        Sort sort = Sort.by(sortField);
        if(sortDir.equals("asc")) sort = sort.ascending();
        else if(sortDir.equals("desc")) sort = sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, QUESTION_PER_PAGE, sort);
        return repo.findByProduct(productId, pageable);
    }

    public Page<Question> list3MostVotedQuestions(Product product) {
        int productId = product.getId();
        Sort sort = Sort.by("votes").descending();
        Pageable pageable = PageRequest.of(0, 3, sort);
        return repo.findByProduct(productId, pageable);
    }

    public int countOfAnsweredQuestions(Product product) {
        int productId = product.getId();
        return repo.countAnsweredQuestionByProduct(productId);
    }
}
