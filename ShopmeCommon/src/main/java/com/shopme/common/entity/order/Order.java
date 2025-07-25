package com.shopme.common.entity.order;

import com.shopme.common.entity.AbstractAddress;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends AbstractAddress {
    @Column(nullable = false, length = 45)
    private String country;

    private Date orderTime;

    private float shippingCost;

    private float productCost;

    private float subTotal;

    private float tax;

    private float total;

    private int deliverDays;
    private Date deliverDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @OrderBy("updatedTime")
    private List<OrderTrack> orderTracks = new ArrayList<>();

    public void copyAddressFromCustomer() {
        this.setFirstName(customer.getFirstName());
        this.setLastName(customer.getLastName());
        this.setPhoneNumber(customer.getPhoneNumber());
        this.setAddressLine1(customer.getAddressLine1());
        this.setAddressLine2(customer.getAddressLine2());
        this.setCity(customer.getCity());
        this.setCountry(customer.getCountry().getName());
        this.setPostalCode(customer.getPostalCode());
        this.setState(customer.getState());
    }

    public void copyShippingAddressFromAddress(Address address) {
        this.setFirstName(address.getFirstName());
        this.setLastName(address.getLastName());
        this.setPhoneNumber(address.getPhoneNumber());
        this.setAddressLine1(address.getAddressLine1());
        this.setAddressLine2(address.getAddressLine2());
        this.setCity(address.getCity());
        this.setCountry(address.getCountry().getName());
        this.setPostalCode(address.getPostalCode());
        this.setState(address.getState());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer.getFullName() +
                ", orderStatus=" + orderStatus +
                ", subTotal=" + subTotal +
                ", paymentMethod=" + paymentMethod +
                '}';
    }

    @Transient
    public String getDestination() {
        return String.format("%s, %s, %s", city, state, country);
    }


    @Transient
    public String getShippingAddress() {
        String address = firstName;
        if(lastName != null && !lastName.isEmpty())  address += " " + lastName;

        if(!addressLine1.isEmpty()) address += ", " + addressLine1;

        if(addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;

        if(!city.isEmpty()) address += ", " + city;

        if(state != null && !state.isEmpty()) address += ", " + state;

        address += ", " + country;

        if(!postalCode.isEmpty()) address += ". Postal code: " + postalCode;

        if(!phoneNumber.isEmpty()) address += ". Phone Number: " + phoneNumber;

        return address;
    }


}

