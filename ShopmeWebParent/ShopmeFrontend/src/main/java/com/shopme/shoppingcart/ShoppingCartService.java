package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShoppingCartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ShoppingCartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> listCartItems(Customer customer) {
        return cartRepository.findByCustomer(customer);
    }

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);
        CartItem cartItem = cartRepository.findByCustomerAndProduct(customer, product);

        if(cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
            if(updatedQuantity > 5) {
                throw new ShoppingCartException(String.format("Could not add more %d item(s) because there's already %d items in your shopping cart. " +
                        "Maximum allowed quantity is 5.", quantity, cartItem.getQuantity()));
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

    public float updateQuantity(Integer productId, Integer quantity, Customer customer) throws ProductNotFoundException {
        cartRepository.updateQuantity(quantity, customer.getId(), productId);

        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()) {
            Product savedProduct = product.get();
            return savedProduct.getDiscountPrice() * quantity;
        }else {
            throw new ProductNotFoundException("Could not found any product with ID " + product);
        }
    }





    public void removeProduct(Integer productId, Customer customer) {
        cartRepository.deleteByCustomerAndProduct(customer.getId(), productId);
    }
}
