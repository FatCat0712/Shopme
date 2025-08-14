package com.shopme.question;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.question.Question;
import com.shopme.question.ask.QuestionCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository repo;
    public static final int QUESTION_PER_PAGE = 4;

    @Autowired
    public QuestionService(QuestionRepository repo) {
        this.repo = repo;
    }

    public Page<Question> listByCustomer(Customer customer, int pageNum, String sortDir, String keyword) {
        int customerID = customer.getId();
        Sort sort = Sort.by("askTime");
        if(sortDir.equals("asc")) sort = sort.ascending();
        else if(sortDir.equals("desc")) sort = sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, QUESTION_PER_PAGE, sort);

        Page<Question> page;
        if(keyword != null) {
            page = repo.findByCustomerAndKeyword(keyword, customerID, pageable);
        }
        else {
            page = repo.findByCustomer(customerID, pageable);
        }

        return page;
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

    public void saveQuestion(QuestionCreateRequest request, Customer customer) throws DuplicateQuestion {
            Integer customerID = customer.getId();
            String questionContent = request.getQuestionContent();
            Integer productId = request.getProductId();
            Integer matchContentCount = repo.countExactContentMatch(productId, questionContent);
            if(matchContentCount > 0) {
                throw new DuplicateQuestion("This question is already exists for this product");
            }
            Question newQuestion = new Question();
            newQuestion.setQuestionContent(questionContent);
            newQuestion.setAsker(new Customer(customerID));
            newQuestion.setAskTime(new Date());
            newQuestion.setApprovalStatus(false);
            newQuestion.setProduct(new Product(productId));
            repo.save(newQuestion);
    }

    public Question get(Integer questionId) throws QuestionNotFound {
        Optional<Question> question = repo.findById(questionId);
        if(question.isEmpty()) {
            throw new QuestionNotFound("Could not find any questions with ID " + question);
        }
        return question.get();
    }
}
