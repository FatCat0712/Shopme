package com.shopme.common.entity.order;

import com.shopme.common.entity.AbstractAddress;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private float subtotal;

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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedTime ASC")
    private List<OrderTrack> orderTracks = new ArrayList<>();

    public Order() {
    }

    public Order(Integer id , Date orderTime, float productCost, float subtotal, float total) {
        this.id = id;
        this.orderTime = orderTime;
        this.productCost = productCost;
        this.subtotal = subtotal;
        this.total = total;
    }

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
                ", subTotal=" + subtotal +
                ", paymentMethod=" + paymentMethod +
                '}';
    }

    @Transient
    public String getDestination() {
        return String.format("%s %s %s", city != null && !city.isEmpty() ? city + "," : "", state != null ? state + "," : "", country);
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

    @Transient
    public String getDeliverDateOnForm() {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormatter.format(this.deliverDate);
    }

    public void setDeliverDateOnForm(String dateString) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.deliverDate = dateFormatter.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Transient
    public String getOrderTimeOnForm() {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormatter.format(this.orderTime);
    }

    @Transient
    public String getRecipientName() {
        String name = firstName;
        if(lastName != null && !lastName.isEmpty())  name += " " + lastName;
        return name;
    }

    @Transient
    public String getRecipientAddress() {
        String address = addressLine1;
        if(addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;

        if(!city.isEmpty()) address += ", " + city;

        if(state != null && !state.isEmpty()) address += ", " + state;

        address += ", " + country;

        if(!postalCode.isEmpty()) address += ". Postal code: " + postalCode;

        return address;
    }

    @Transient
    public boolean isCOD() {
        return paymentMethod.equals(PaymentMethod.COD);
    }


    @Transient
    public boolean isNew() {return hasStatus(OrderStatus.NEW);}

    @Transient
    public boolean isProcessing() {
        return hasStatus(OrderStatus.PROCESSING);
    }

    @Transient
    public boolean isPicked() {
        return hasStatus(OrderStatus.PICKED);
    }

    @Transient
    public boolean isShipping() {
        return hasStatus(OrderStatus.SHIPPING);
    }

    @Transient
    public boolean isDelivered() {
        return hasStatus(OrderStatus.DELIVERED);
    }

    @Transient
    public boolean isReturned() {
        return hasStatus(OrderStatus.RETURNED);
    }

    @Transient
    public boolean isReturnRequested() {
        return hasStatus(OrderStatus.RETURN_REQUESTED);
    }

    @Transient
    public boolean isCancelled() {
        return hasStatus(OrderStatus.CANCELLED);
    }

    @Transient
    public boolean hasStatus(OrderStatus status) {
        return !orderTracks.isEmpty() && orderTracks.getLast().getStatus().equals(status);
    }



    @Transient
    public String getProductNames() {
        String productNames = "";

        productNames = "<ul>";

        for(OrderDetail detail : orderDetails) {
            productNames += "<li>" + detail.getProduct().getShortName() + "</li>";
        }

        productNames += "</ul>";

        return productNames;

    }


}

