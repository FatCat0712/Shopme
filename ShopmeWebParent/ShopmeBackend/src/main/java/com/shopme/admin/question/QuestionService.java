package com.shopme.admin.question;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.question.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository repo;
    public final static int QUESTION_PER_PAGE = 10;

    @Autowired
    public QuestionService(QuestionRepository repo) {
        this.repo = repo;
    }

    public List<Question> listAll() {
        return repo.findAll();
    }

    public void listByPage(PagingAndSortingHelper helper, int pageNum) {
        helper.listEntities(pageNum, QUESTION_PER_PAGE, repo);
    }

    public Question get(Integer id) throws QuestionNotFound {
        Optional<Question> question = repo.findById(id);
        if(question.isEmpty()) {
            throw new QuestionNotFound("Could not find any questions with ID " + id);
        }
        return question.get();
    }

    public void approveQuestion(Integer id, Boolean isApproved) throws QuestionNotFound {
        try {
            Question questionInDB = get(id);
            questionInDB.setApprovalStatus(isApproved);
            repo.save(questionInDB);
        } catch (QuestionNotFound e) {
            throw new QuestionNotFound(e.getMessage());
        }
    }

    public void save(Question questionInForm) throws QuestionNotFound {
        int questionId = questionInForm.getId();
        Optional<Question> question = repo.findById(questionId);
        if(question.isEmpty()) {
            throw new QuestionNotFound("Could not save question with ID " + questionId);
        }
        Question questionInDB = question.get();
        questionInDB.setQuestionContent(questionInForm.getQuestionContent());
        if(!questionInForm.getAnswerContent().isEmpty()) {
            questionInDB.setAnswerContent(questionInForm.getAnswerContent());
        }
        questionInDB.setApprovalStatus(questionInForm.isApprovalStatus());
        questionInDB.setAnswerTime(new Date());
        questionInDB.setAnswerer(questionInForm.getAnswerer());
        repo.save(questionInDB);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }


}
