package com.shopme.security.oauth;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import com.shopme.shoppingcart.ShoppingCartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CustomerService customerService;
    private final ShoppingCartService shoppingCartService;

    @Autowired
    public OAuth2LoginSuccessHandler(@Lazy CustomerService customerService, ShoppingCartService shoppingCartService) {
        this.customerService = customerService;
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
        HttpSession session = request.getSession(false);

        String name = oAuth2User.getName();
        String email = oAuth2User.getEmail();
        String countryCode = request.getLocale().getCountry();

        String clientName = oAuth2User.getClientName();
        AuthenticationType authenticationType = getAuthenticationType(clientName);

        Customer customer = customerService.findCustomerByEmail(email);

        if(customer == null) {
           Customer newCustomer =  customerService.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
            session.setAttribute("email", newCustomer.getEmail());
            session.setAttribute("sum", 0);
            session.setAttribute("avatar", newCustomer.getAvatarPath());
        }
        else {
            oAuth2User.setFullName(customer.getFullName());
            customerService.updateAuthenticationType(customer, authenticationType);
            session.setAttribute("email", customer.getEmail());
            Integer cartQuantity = shoppingCartService.fetchCartQuantityByCustomer(customer);
            session.setAttribute("sum", cartQuantity != null ? cartQuantity : 0);
            session.setAttribute("avatar", customer.getAvatarPath());
        }



        super.onAuthenticationSuccess(request, response, authentication);

    }


    private AuthenticationType getAuthenticationType(String clientName) {
        if(clientName.equals("Google")) {
            return AuthenticationType.GOOGLE;
        }
        else if(clientName.equals("Facebook")) {
            return AuthenticationType.FACEBOOK;
        }
        else {
            return AuthenticationType.DATABASE;
        }
    }
}
