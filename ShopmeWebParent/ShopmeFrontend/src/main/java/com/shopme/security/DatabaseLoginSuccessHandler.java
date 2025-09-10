package com.shopme.security;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import com.shopme.shoppingcart.ShoppingCartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CustomerService customerService;
    private final ShoppingCartService shoppingCartService;

    public DatabaseLoginSuccessHandler(@Lazy  CustomerService customerService, ShoppingCartService shoppingCartService) {
        this.customerService = customerService;
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
            HttpSession session = request.getSession(false);
            CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
            Customer customer = customerDetails.getCustomer();

            if(customer != null) {
                session.setAttribute("email", customer.getEmail());
                Integer cartQuantity = shoppingCartService.fetchCartQuantityByCustomer(customer);
                session.setAttribute("sum", cartQuantity != null ? cartQuantity : 0);
                session.setAttribute("avatar", customer.getAvatarPath());
                customerService.updateAuthenticationType(customer, AuthenticationType.DATABASE);
            }

            super.onAuthenticationSuccess(request, response, authentication);

    }
}
