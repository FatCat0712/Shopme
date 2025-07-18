package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {
    private final CartRepository cartRepository;

    @Autowired
    public ShoppingCartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);
        CartItem cartItem = cartRepository.findByCustomerAndProduct(customer, product);

        if(cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
            if(updatedQuantity > 5) {
                throw new ShoppingCartException(String.format("Could not add more %d item(s) because there's already %d items in your shopping cart. " +
                        "Maximum allowed quantity is 5.", updatedQuantity, cartItem.getQuantity()));
            }
        }
        else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCustomer(customer);
        }

        cartItem.setQuantity(updatedQuantity);
        cartRepository.save(cartItem);
        return updatedQuantity;
    }
}
