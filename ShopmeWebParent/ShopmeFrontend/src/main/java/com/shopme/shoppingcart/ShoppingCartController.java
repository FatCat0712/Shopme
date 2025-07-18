package com.shopme.shoppingcart;

import com.shopme.Utility;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ShoppingCartController {
    private final ShoppingCartService cartService;
    private final CustomerService customerService;

    @Autowired
    public ShoppingCartController(ShoppingCartService cartService, CustomerService customerService) {
        this.cartService = cartService;
        this.customerService = customerService;
    }

    @GetMapping("/cart")
    public String viewCart(HttpServletRequest request, Model model) {

        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartService.listCartItems(customer);
        Float estimatedTotal = cartItems.stream().map(CartItem::getSubTotal).reduce(0F, Float::sum);

        model.addAttribute("estimatedTotal", estimatedTotal);
        model.addAttribute("listCartItems", cartItems);
        return "cart/shopping_cart";
    }


    private Customer getAuthenticatedCustomer(HttpServletRequest request){
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.findCustomerByEmail(email);
    }


}
