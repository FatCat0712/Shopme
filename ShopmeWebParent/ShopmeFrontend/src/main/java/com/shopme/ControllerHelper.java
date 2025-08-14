package com.shopme;

import com.shopme.common.entity.Customer;
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

    public Customer getAuthenticatetdCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.findCustomerByEmail(email);
    }

}
