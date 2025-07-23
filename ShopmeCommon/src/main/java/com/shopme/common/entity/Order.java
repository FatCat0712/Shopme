package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "address_line1", nullable = false, length = 64)
    private String addressLine1;

    @Column(name = "address_line2", length = 64)
    private String addressLine2;

    @Column(nullable = false, length = 45)
    private String city;

    @Column(nullable = false, length = 45)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

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
}
