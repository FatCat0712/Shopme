package com.shopme.customer;

import com.shopme.Utility;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerDetails;
import com.shopme.security.oauth.CustomerOAuth2User;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CustomerController {
    private final CustomerService customerService;
    private final SettingService settingService;

    @Autowired
    public CustomerController(CustomerService customerService, SettingService settingService) {
        this.customerService = customerService;
        this.settingService = settingService;
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
            List<Country> listCountries = customerService.listAllCountries();
            Customer customer = new Customer();

            model.addAttribute("pageTitle","Customer Registration");
            model.addAttribute("listCountries", listCountries);
            model.addAttribute("customer", customer);

            return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(HttpServletRequest request, Customer customer,  Model model) throws MessagingException, UnsupportedEncodingException {
        customerService.registerCustomer(customer);
        sendVerificationEmail(request, customer);
        model.addAttribute("pageTitle", "Registration Succeeded");
        return "/register/register_success";
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam(name = "code") String code, Model model) {
        boolean verified = customerService.verify(code);

        return "register/" + (verified ? "verify_success" : "verify_fail");
    }


    private void sendVerificationEmail(HttpServletRequest request , Customer customer) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = customer.getEmail();
        String subject = emailSettings.getCustomerVerifySubject();
        String content = emailSettings.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", customer.getFullName());

        String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }

    @GetMapping("/account_details")
    public String viewAccount(Model model, HttpServletRequest request) {
        String customerEmail = Utility.getEmailOfAuthenticatedCustomer(request);
        Customer customer = customerService.findCustomerByEmail(customerEmail);
        List<Country> listCountries = customerService.listAllCountries();
        model.addAttribute("customer", customer);
        model.addAttribute("listCountries", listCountries);
        return "customer/account_form";
    }

    @PostMapping("/update_account_details")
    public String updateAccountDetails(Customer customer, HttpServletRequest request, RedirectAttributes ra, Model model) {
        try{
            Customer updatedCustomer = customerService.update(customer);
            model.addAttribute("customer", updatedCustomer);
            updateNameForAuthenticatedCustomer(customer, request);
            ra.addFlashAttribute("message", "The account details have been updated successfully");
        }catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/account_details";

    }



    private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        if(principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken){
            CustomerDetails customerDetails = getCustomerUserDetailsObject(principal);
            Customer authenticatedCustomer = customerDetails.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());
        }
        else if(principal instanceof OAuth2AuthenticationToken oauth2Token) {
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
            oAuth2User.setFullName(fullName);
        }

    }

    private CustomerDetails getCustomerUserDetailsObject(Object principal) {
        CustomerDetails customerDetails = null;
        if(principal instanceof UsernamePasswordAuthenticationToken token) {
            customerDetails = (CustomerDetails)token.getPrincipal();
        }else if(principal instanceof RememberMeAuthenticationToken token) {
            customerDetails = (CustomerDetails)token.getPrincipal();
        }
        return customerDetails;
    }
}
