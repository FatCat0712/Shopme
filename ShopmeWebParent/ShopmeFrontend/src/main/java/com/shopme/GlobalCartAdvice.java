package com.shopme;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import com.shopme.shoppingcart.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalCartAdvice {
    private final CustomerService customerService;
    private final ShoppingCartService shoppingCartService;
    private final ControllerHelper controllerHelper;

    @Autowired
    public GlobalCartAdvice(
            CustomerService customerService,
            ShoppingCartService shoppingCartService,
            ControllerHelper controllerHelper
    ) {
        this.customerService = customerService;
        this.shoppingCartService = shoppingCartService;
        this.controllerHelper = controllerHelper;
    }

    @ModelAttribute("listCartItems")
    public List<CartItem> populateCart(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null) return List.of();

        Object email = session.getAttribute("email");
        if(email == null) return List.of();

        Customer customer = controllerHelper.getAuthenticatetdCustomer(request);
        if(customer == null) return List.of();

        List<CartItem> listCartItems = shoppingCartService.listCartItems(customer);

        return listCartItems.isEmpty() ? List.of() : listCartItems;
    }
}
