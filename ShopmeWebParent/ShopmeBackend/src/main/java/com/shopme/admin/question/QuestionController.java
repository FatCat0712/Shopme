package com.shopme.admin.question;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.question.export.QuestionCsvExporter;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.question.Question;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;
    private final String defaultURL = "redirect:/questions/page/1?sortField=askTime&sortDir=asc";

    @Autowired
    public QuestionController(QuestionService questionService, UserService userService) {
        this.questionService = questionService;
        this.userService = userService;
    }

    @GetMapping("/questions")
    public String listFirstPage() {
        return defaultURL;
    }

    @GetMapping("/questions/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(moduleURL = "/questions", listName = "listQuestions")PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") Integer pageNum,
            Model model
    )
    {
        questionService.listByPage(helper, pageNum);
        model.addAttribute("pageTitle", "Manage Questions");
        return "questions/questions";
    }


    @GetMapping("/questions/detail/{id}")
    public String viewQuestionDetail(@PathVariable(name = "id") Integer id, RedirectAttributes ra, Model model) {
        try {
            Question question =  questionService.get(id);
            model.addAttribute("question", question);
            return "questions/question_detail_modal";
        } catch (QuestionNotFound e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return defaultURL;
        }
    }

    @GetMapping("/questions/edit/{id}")
    public String editQuestion(@PathVariable(name = "id") Integer id, RedirectAttributes ra, Model model) {
        try {
            Question question =  questionService.get(id);
            model.addAttribute("question", question);
            model.addAttribute("pageTitle", String.format("Edit Question (ID: %d)", id));
            return "questions/question_form";
        } catch (QuestionNotFound e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return defaultURL;
        }
    }

    @GetMapping("/questions/{id}/approved/{status}")
    public String approveQuestion(@PathVariable(name = "id") Integer questionId, @PathVariable(name = "status") Boolean isApproved, RedirectAttributes ra) {
        try {
            questionService.approveQuestion(questionId, isApproved);
            ra.addFlashAttribute("message", String.format("The question with ID %d was %s", questionId, isApproved ? "approved" : "rejected"));
        } catch (QuestionNotFound e) {
           ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultURL;
    }

    @PostMapping("/questions/save")
    public String saveQuestion(
            Question questionInform,
            Authentication authentication,
            RedirectAttributes ra
    ) {
        try {
            User user = userService.getByEmail(authentication.getName());
            questionInform.setAnswerer(new User(user.getId()));
            questionService.save(questionInform);
            ra.addFlashAttribute("message", String.format("The question with ID %d has been saved", questionInform.getId()));
        } catch (QuestionNotFound e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultURL;
    }

    @GetMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable(name = "id") Integer questionId, RedirectAttributes ra) {
        questionService.delete(questionId);
        ra.addFlashAttribute("message", String.format("The question with ID %d has been deleted", questionId));
        return defaultURL;
    }

    @GetMapping("/questions/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Question> listQuestions = questionService.listAll();
        QuestionCsvExporter exporter = new QuestionCsvExporter();
        exporter.export(listQuestions, response);
    }

}
