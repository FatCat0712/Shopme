package com.shopme;

import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControllerHelper {
    private final CustomerService customerService;

    @Autowired
    public ControllerHelper(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Customer getAuthenticatetdCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        Customer customer = customerService.findCustomerByEmail(email);
        if(customer == null) {
            throw new CustomerNotFoundException("");
        }
        return customer;
    }

}
