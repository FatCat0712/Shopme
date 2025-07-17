package com.shopme.security;

import com.shopme.common.entity.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class ShopmeCustomerDetails implements UserDetails {

    private  Customer customer;

    public ShopmeCustomerDetails(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return customer.isEnabled();
    }

    public String getFullName() {
        return  customer.getFirstName() + " " + customer.getLastName();
    }

    public Customer getCustomer() {
        return customer;
    }

}
