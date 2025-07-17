package com.shopme.customer;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {
    private final CustomerService customerService;
    private final SettingService settingService;

    @Autowired
    public ForgotPasswordController(CustomerService customerService, SettingService settingService) {
        this.customerService = customerService;
        this.settingService = settingService;
    }

    @GetMapping("/forgot_password")
    public String showRequestForm() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processRequestForm(HttpServletRequest request, Model model)  {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(link, email);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check");
        }
        catch (MessagingException | UnsupportedEncodingException | CustomerNotFound e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "customer/forgot_password_form";
    }

    private void sendEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String subject = "Here's the link to reset your password";
        String content = "<p>Hello,<p>" +
                "<p>You have requested to reset your password.</p>" +
                "<p>Click the link below to change your password</p>" +
                "<p><a href=\"" + link +   "\">Change my password</a></p>" +
                "<br>" +
                "<p>Ignore this email if you do remember your password, or your have not made the request.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(email);
        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);

    }

    @GetMapping("/reset_password")
    public String showResetForm(@RequestParam(name = "token") String resetPasswordToken, Model model) {
        Customer customer = customerService.getCustomerByResetPasswordToken(resetPasswordToken);
        if(customer != null) {
            model.addAttribute("token", resetPasswordToken);
        }
        else {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("errorMessage", "Invalid Token");
            return "message";
        }
        return "customer/reset_password_form";

    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        try {
            customerService.updatePassword(token, password);
            model.addAttribute("message", "You have changed your password successfully");
            model.addAttribute("title", "Reset Your Password");
            model.addAttribute("pageTitle", "Reset Your Password");
        } catch (CustomerNotFound e) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "message";
    }


}
