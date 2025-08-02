package com.shopme.order;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.OrderNotFoundException;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {
    private final OrderService orderService;
    private final CustomerService customerService;

    public OrderRestController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    @PostMapping("/orders/return")
    public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest, HttpServletRequest servletRequest) {
        System.out.println("Order ID: " + returnRequest.getOrderId());
        System.out.println("Reason: " + returnRequest.getReason());
        System.out.println("Note: " + returnRequest.getNote());
        Customer customer;
        try {
            customer = getAuthenticatetdCustomer(servletRequest);
            orderService.setOrderReturnRequest(returnRequest, customer);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
    }


    private Customer getAuthenticatetdCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if(email == null) {
            throw  new CustomerNotFoundException("No authenticated customer");
        }
        return customerService.findCustomerByEmail(email);
    }


}
