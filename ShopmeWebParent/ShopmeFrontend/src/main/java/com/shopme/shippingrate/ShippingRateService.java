package com.shopme.shippingrate;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingRateService {
    private final ShippingRateRepository shippingRateRepository;

    @Autowired
    public ShippingRateService(ShippingRateRepository shippingRateRepository) {
        this.shippingRateRepository = shippingRateRepository;
    }

    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();
        if(state == null || state.isEmpty()) {
            state = customer.getCity();
        }
        Country country = customer.getCountry();
        return shippingRateRepository.findByCountryAndState(country, state);
    }

    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if(state == null || state.isEmpty()) {
            state = address.getCity();
        }
        Country country = address.getCountry();
        return shippingRateRepository.findByCountryAndState(country, state);
    }







}
