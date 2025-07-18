package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CartItemShoppingRepositoryTest {
    private final CartRepository cartRepository;
    private final TestEntityManager testEntityManager;

    @Autowired
    public CartItemShoppingRepositoryTest(CartRepository cartRepository, TestEntityManager testEntityManager) {
        this.cartRepository = cartRepository;
        this.testEntityManager = testEntityManager;
    }

    @Test
    public void testSaveItem(){
        Integer customerId = 1;
        Integer productId = 1;
        Customer customer = testEntityManager.find(Customer.class, customerId);
        Product product = testEntityManager.find(Product.class, productId);

        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        CartItem savedItem = cartRepository.save(cartItem);

        assertThat(savedItem.getId()).isGreaterThan(0);
    }

    @Test
    public void testSaveTwoItems() {
        Integer customerId = 10;
        Integer productId = 10;

        Customer customer = testEntityManager.find(Customer.class, customerId);
        Product product = testEntityManager.find(Product.class, productId);

        CartItem cartItem1 = new CartItem();
        cartItem1.setCustomer(customer);
        cartItem1.setProduct(product);
        cartItem1.setQuantity(2);

        CartItem cartItem2 = new CartItem();
        cartItem2.setCustomer(new Customer(customerId));
        cartItem2.setProduct(new Product(8));
        cartItem2.setQuantity(3);

        Iterable<CartItem> cartItems = cartRepository.saveAll(List.of(cartItem1, cartItem2));

        assertThat(cartItems).size().isGreaterThan(0);

    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 10;
        List<CartItem> listItems = cartRepository.findByCustomer(new Customer(customerId));
        listItems.forEach(System.out::println);
        assertThat(listItems.size()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomerAndProduct() {
        Integer customerId = 1;
        Integer productId = 1;

        CartItem cartItem = cartRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(cartItem).isNotNull();

        System.out.println(cartItem);
    }

    @Test
    public void testUpdateQuantity() {
        Integer customerId = 1;
        Integer productId = 1;
        Integer quantity = 4;

        cartRepository.updateQuantity(quantity, customerId, productId);

        CartItem cartItem = cartRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(cartItem.getQuantity()).isEqualTo(quantity);

    }

    @Test
    public void testDeleteByCustomerAndProduct() {
        Integer customerId = 10;
        Integer productId = 10;

        cartRepository.deleteByCustomerAndProduct(customerId, productId);

        CartItem cartItem = cartRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(cartItem).isNull();
    }





}
