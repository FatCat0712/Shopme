package com.shopme.question.ask;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.customer.CustomerService;
import com.shopme.question.DuplicateQuestion;
import com.shopme.question.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionCreateRestController {
    private final QuestionService questionService;
    private final CustomerService customerService;

    @Autowired
    public QuestionCreateRestController(QuestionService questionService, CustomerService customerService) {
        this.questionService = questionService;
        this.customerService = customerService;
    }

    @PostMapping("/questions/save")
    public ResponseEntity<?> askQuestion(@RequestBody QuestionCreateRequest questionRequest, HttpServletRequest request) {
        Customer customer;
        try {
            customer = getAuthenticatetdCustomer(request);
            questionService.saveQuestion(questionRequest, customer);
        } catch (CustomerNotFoundException e) {
           return new ResponseEntity<>(new QuestionCreateResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (DuplicateQuestion e) {
            return new ResponseEntity<>(new QuestionCreateResponse(false, e.getMessage()), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new QuestionCreateResponse(true, "Your question has been posted and awaiting for approval."), HttpStatus.OK);

    }

    private Customer getAuthenticatetdCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if(email == null) {
            throw new CustomerNotFoundException("You must login first");
        }
        return customerService.findCustomerByEmail(email);
    }
}
