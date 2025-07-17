package com.shopme.security;

import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer saveCustomer = customerRepository.findByEmail(email);
        if(saveCustomer != null) {
            return new CustomerDetails(saveCustomer);
        }
        else {
            throw  new UsernameNotFoundException("Could not find any customer with email: " + email);
        }

    }
}
