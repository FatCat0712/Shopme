package com.shopme.order;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.*;
import com.shopme.common.entity.product.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
            Order newOrder = new Order();

            newOrder.setOrderTime(new Date());

            if(paymentMethod.equals(PaymentMethod.PAYPAL)) {
                newOrder.setOrderStatus(OrderStatus.PAID);
            }else {
                newOrder.setOrderStatus(OrderStatus.NEW);
            }

            newOrder.setCustomer(customer);
            newOrder.setProductCost(checkoutInfo.getProductCost());
            newOrder.setSubTotal(checkoutInfo.getProductTotal());
            newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
            newOrder.setTax(0.0f);
            newOrder.setTotal(checkoutInfo.getPaymentTotal());
            newOrder.setPaymentMethod(paymentMethod);
            newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
            newOrder.setDeliverDate(checkoutInfo.getDeliverDate());

            if(address == null) {
                newOrder.copyAddressFromCustomer();
            }
            else {
                newOrder.copyShippingAddressFromAddress(address);
            }

            Set<OrderDetail> orderDetails =  newOrder.getOrderDetails();

            for(CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(newOrder);
                orderDetail.setProduct(product);
                orderDetail.setQuantity(cartItem.getQuantity());
                orderDetail.setUnitPrice(product.getDiscountPrice());
                orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
                orderDetail.setSubTotal(cartItem.getSubTotal());
                orderDetail.setShippingCost(cartItem.getShippingCost());
                orderDetails.add(orderDetail);
            }

            List<OrderTrack> orderTracks = newOrder.getOrderTracks();
            OrderTrack orderTrack = new OrderTrack();
            orderTrack.setStatus(OrderStatus.NEW);
            orderTrack.setUpdatedTime(new Date());
            orderTrack.setNotes(OrderStatus.NEW.defaultDescription());
            orderTrack.setOrder(newOrder);
            orderTracks.add(orderTrack);

            newOrder.setOrderTracks(orderTracks);


            return orderRepository.save(newOrder);

    }


}
