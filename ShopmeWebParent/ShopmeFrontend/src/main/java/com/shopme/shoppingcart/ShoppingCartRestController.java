package com.shopme.shoppingcart;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.NotEnoughStockException;
import com.shopme.customer.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShoppingCartRestController {
    private final ShoppingCartService cartService;
    private final ControllerHelper controllerHelper;

    @Autowired
    public ShoppingCartRestController(ShoppingCartService cartService, ControllerHelper controllerHelper) {
        this.cartService = cartService;
        this.controllerHelper = controllerHelper;
    }

    @PostMapping("/cart/add/{productId}/{quantity}")
    public ShoppingCartDTO addProductToCart(@PathVariable(name = "productId") Integer productId, @PathVariable(name = "quantity") Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = controllerHelper.getAuthenticatetdCustomer(request);
            Integer updatedQuantity = null;
            updatedQuantity = cartService.addProduct(productId, quantity, customer);
            Integer cartSum = cartService.fetchCartQuantityByCustomer(customer);
            return new ShoppingCartDTO(cartSum, updatedQuantity + " item(s) of this product were added to your shopping cart.");
        } catch (CustomerNotFoundException e) {
                return new ShoppingCartDTO("You must login to add this product to cart");
        }catch (ShoppingCartException e) {
            return new ShoppingCartDTO(e.getMessage());
        }
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(
            @PathVariable(name = "productId") Integer productId,
            @PathVariable(name = "quantity") Integer quantity,
            HttpServletRequest request) throws CustomerNotFoundException {
        Customer customer = controllerHelper.getAuthenticatetdCustomer(request);
        Float newSubTotal = cartService.updateQuantity(productId, quantity, customer);
       return String.valueOf(newSubTotal);
    }

    @GetMapping("/cart/validate")
    public String validateCart(HttpServletRequest request) throws CustomerNotFoundException, NotEnoughStockException {
        Customer customer = controllerHelper.getAuthenticatetdCustomer(request);
        List<CartItem> listCartItems = cartService.listCartItems(customer);
        boolean isValid = true;
        Product currentProduct = null;
        for(CartItem cartItem: listCartItems) {
            Product product = cartItem.getProduct();
            if(cartItem.getQuantity() > product.getStockQuantity()) {
                currentProduct = product;
               isValid = false;
               break;
            }
        }

        if(!isValid) {
            return String.format("Only %d items left for %s", currentProduct.getStockQuantity(), currentProduct.getShortName());
        }
        else {
            return "ok";
        }
    }


    @DeleteMapping("/cart/remove/{productId}")
    public ShoppingCartDTO removeProductFromCart(@PathVariable(name = "productId") Integer productId, HttpServletRequest request) throws CustomerNotFoundException {
            HttpSession session = request.getSession(false);
            Customer customer = controllerHelper.getAuthenticatetdCustomer(request);
            cartService.removeProduct(productId, customer);
            Integer cartSum = cartService.fetchCartQuantityByCustomer(customer);
            session.setAttribute("sum", cartSum);
            return new ShoppingCartDTO(cartSum, "The product has been removed from your shopping cart");
    }



}
