package com.shopme.order;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.*;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.OrderNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;

    public static int ORDER_PER_PAGE  = 5;

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

    public Page<Order> listForCustomerByPage(Customer customer, int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

         Pageable pageable = PageRequest.of(pageNum - 1, ORDER_PER_PAGE, sort);
         Page<Order> page = null;
         int customerId = customer.getId();
         if(keyword != null) {
             page  = orderRepository.findAll(keyword, customerId, pageable);
         }
         else {
             page = orderRepository.findAll(customerId, pageable);
         }

         return page;
    }

    public Order getOrder(int orderId, Customer customer) throws OrderNotFoundException {
        Order order =  orderRepository.findByIdAndCustomer(orderId, customer);
        if(order == null) {
            throw new OrderNotFoundException("Could not find any order with ID " + orderId);
        }
        else {
            return order;
        }
    }

    public void setOrderReturnRequest(OrderReturnRequest request, Customer customer) throws OrderNotFoundException {
        Order order = orderRepository.findByIdAndCustomer(request.getOrderId(), customer);

        if(order == null) {
            throw new OrderNotFoundException("Could not find any order with ID " + request.getOrderId());
        }

        if(order.isReturnRequested()) return;

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setUpdatedTime(new Date());
        track.setStatus(OrderStatus.RETURN_REQUESTED);

        String notes = "Reason: " + request.getReason();
        if(!"".equals(request.getNote())) {
            notes += ". " + request.getNote();
        }

        track.setNotes(notes);

        order.getOrderTracks().add(track);
        order.setOrderStatus(OrderStatus.RETURN_REQUESTED);

        orderRepository.save(order);

    }


}
