package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 45, nullable = false, unique = true)
    private String email;

    @Column(length = 64, nullable = false)
    private String password;

    @Column(name="first_name", length = 45, nullable = false)
    private String firstName;

    @Column(name="last_name", length = 45, nullable = false)
    private String lastName;

    @Column(name="phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name="address_line1", length = 64, nullable = false)
    private String addressLine1;

    @Column(name="address_line2", length = 64)
    private String addressLine2;

    @Column(length = 45, nullable = false)
    private String city;

    @Column(length = 45, nullable = false)
    private String state;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", length = 10)
    private AuthenticationType authenticationType;

    @Column(name = "reset_password_token", length = 30)
    private String resetPasswordToken;

    @Column(name="postal_code", length = 10, nullable = false)
    private String postalCode;

    @Column(name="created_time")
    private Date createdTime;

    @Column(name="enabled", nullable = false)
    private boolean isEnabled;

    @Column(name="verification_code", length = 64)
    private String verificationCode;


    public Customer() {
    }

    public Customer(Integer id) {
        this.id = id;
    }

    public Customer(Integer id, String email, String password, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", country=" + country +
                ", postalCode='" + postalCode + '\'' +
                ", createdTime=" + createdTime +
                ", isEnabled=" + isEnabled +
                ", verificationCode='" + verificationCode + '\'' +
                '}';
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public String getAddress() {
        String address = firstName;
        if(lastName != null && !lastName.isEmpty())  address += " " + lastName;

        if(!addressLine1.isEmpty()) address += ", " + addressLine1;

        if(addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;

        if(!city.isEmpty()) address += ", " + city;

        if(state != null && !state.isEmpty()) address += " " + state;

        address += ", " + country.getName();

        if(!postalCode.isEmpty()) address += ". Postal code: " + postalCode;

        if(!phoneNumber.isEmpty()) address += ". Phone Number: " + phoneNumber;

        return address;
    }

}
