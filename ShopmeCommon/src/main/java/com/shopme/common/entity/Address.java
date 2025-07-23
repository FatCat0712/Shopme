package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address extends AbstractAddressWithCountry {
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;


    @Column(name = "default_address")
    private boolean defaultForShipping;



}
