package com.shopme.admin.order;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.*;
import com.shopme.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepositoryTests {
    private final OrderRepository orderRepository;
    private final TestEntityManager entityManager;

    @Autowired
    public OrderRepositoryTests(OrderRepository orderRepository, TestEntityManager entityManager) {
        this.orderRepository = orderRepository;
        this.entityManager = entityManager;
    }

    @Test
    public void testCreateNewOrderWithSingleProduct() {
        Customer customer = entityManager.find(Customer.class, 2);
        Product product1 = entityManager.find(Product.class,3);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProduct(product1);
        orderDetail1.setOrder(mainOrder);
        orderDetail1.setProductCost(product1.getCost());
        orderDetail1.setShippingCost(10);
        orderDetail1.setQuantity(1);
        orderDetail1.setSubTotal(product1.getPrice());
        orderDetail1.setUnitPrice(product1.getPrice());


        mainOrder.getOrderDetails().add(orderDetail1);
        mainOrder.setShippingCost(30);
        mainOrder.setProductCost(product1.getCost());
        mainOrder.setTax(0);
        float subTotal = product1.getPrice();
        mainOrder.setSubTotal(subTotal);
        mainOrder.setTotal(subTotal);

        mainOrder.setPaymentMethod(PaymentMethod.COD);
        mainOrder.setOrderStatus(OrderStatus.PROCESSING);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(3);

        Order savedOrder = orderRepository.save(mainOrder);
        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewOrderWithMultipleProducts() {
        Customer customer = entityManager.find(Customer.class, 2);
        Product product1 = entityManager.find(Product.class,3);
        Product product2 = entityManager.find(Product.class,5);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProduct(product1);
        orderDetail1.setOrder(mainOrder);
        orderDetail1.setProductCost(product1.getCost());
        orderDetail1.setShippingCost(10);
        orderDetail1.setQuantity(1);
        orderDetail1.setSubTotal(product1.getPrice());
        orderDetail1.setUnitPrice(product1.getPrice());

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProduct(product2);
        orderDetail2.setOrder(mainOrder);
        orderDetail2.setProductCost(product2.getCost());
        orderDetail2.setShippingCost(20);
        orderDetail2.setQuantity(2);
        orderDetail2.setSubTotal(product2.getPrice() * 2);
        orderDetail2.setUnitPrice(product2.getPrice());

        mainOrder.getOrderDetails().add(orderDetail1);
        mainOrder.getOrderDetails().add(orderDetail2);

        mainOrder.setShippingCost(30);
        mainOrder.setProductCost(product1.getCost() + product2.getCost());
        mainOrder.setTax(0);
        float subTotal = product1.getPrice() + product2.getPrice() * 2;
        mainOrder.setSubTotal(subTotal);
        mainOrder.setTotal(subTotal);

        mainOrder.setPaymentMethod(PaymentMethod.COD);
        mainOrder.setOrderStatus(OrderStatus.PROCESSING);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(3);

        Order savedOrder = orderRepository.save(mainOrder);
        assertThat(savedOrder.getId()).isGreaterThan(0);

    }

    @Test
    public void testListOrders() {
       Iterable<Order> orders =  orderRepository.findAll();
       assertThat(orders).hasSizeGreaterThan(0);
       orders.forEach(System.out::println);
    }

    @Test
    public void testUpdateOrder() {
        Integer orderId = 2;
        Optional<Order> order = orderRepository.findById(orderId);
        assertTrue(order.isPresent());
        Order savedOrder = order.get();
        savedOrder.setOrderStatus(OrderStatus.SHIPPING);
        savedOrder.setPaymentMethod(PaymentMethod.COD);
        savedOrder.setOrderTime(new Date());
        savedOrder.setDeliverDays(2);

        Order updatedOrder  = orderRepository.save(savedOrder);

        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.SHIPPING);
    }

    @Test
    public void testGetOrder() {
        Integer orderId = 2;
        Optional<Order> order = orderRepository.findById(orderId);
        assertThat(order.isPresent()).isTrue();
        System.out.println(order.get());
    }

    @Test
    public void testDeleteOrder() {
        Integer orderId = 4;
        orderRepository.deleteById(orderId);
        Optional<Order> order = orderRepository.findById(orderId);
        assertThat(order.isEmpty()).isTrue();
    }

    @Test
    public void testUpdateOrderTracks() {
        Integer orderId = 19;
        Optional<Order> orderById = orderRepository.findById(orderId);
        assertTrue(orderById.isPresent());
        Order order = orderById.get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date());
        newTrack.setStatus(OrderStatus.PICKED);
        newTrack.setNotes(OrderStatus.PICKED.defaultDescription());

        OrderTrack processingTrack = new OrderTrack();
        processingTrack.setOrder(order);
        processingTrack.setUpdatedTime(new Date());
        processingTrack.setStatus(OrderStatus.PACKAGED);
        processingTrack.setNotes(OrderStatus.PACKAGED.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        orderTracks.add(processingTrack);

        Order updatedOrder = orderRepository.save(order);
        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);

    }


}
