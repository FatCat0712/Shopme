package com.shopme.shoppingcart;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartRestController {
    private final ShoppingCartService cartService;
    private final CustomerService customerService;

    @Autowired
    public ShoppingCartRestController(ShoppingCartService shoppingCartService, CustomerService customerService) {
        this.cartService = shoppingCartService;
        this.customerService = customerService;
    }

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable(name = "productId") Integer productId, @PathVariable(name = "quantity") Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = null;
            updatedQuantity = cartService.addProduct(productId, quantity, customer);
            return updatedQuantity + " item(s) of this product were added to your shopping cart.";
        } catch (CustomerNotFoundException e) {
                return "You must login to add this product to cart";
        }catch (ShoppingCartException e) {
            return e.getMessage();
        }
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(
            @PathVariable(name = "productId") Integer productId,
            @PathVariable(name = "quantity") Integer quantity,
            HttpServletRequest request)
    {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Float newSubTotal = cartService.updateQuantity(productId, quantity, customer);
           return String.valueOf(newSubTotal);
        } catch (CustomerNotFoundException e) {
            return "You must login to change quantity of this product";
        }catch (ProductNotFoundException e) {
            return e.getMessage();
        }
    }



    @DeleteMapping("/cart/remove/{productId}")
    public String removeProductFromCart(@PathVariable(name = "productId") Integer productId, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            cartService.removeProduct(productId, customer);
            return "The product has been removed from your shopping cart";
        } catch (CustomerNotFoundException e) {
            return "You must login to remove product";
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);

        if(email == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }

        return customerService.findCustomerByEmail(email);
    }
}
