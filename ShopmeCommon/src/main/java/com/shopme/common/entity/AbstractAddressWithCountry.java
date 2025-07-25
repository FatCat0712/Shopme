package com.shopme.common.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class AbstractAddressWithCountry extends AbstractAddress{
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    protected Country country;

    @Override
    public String toString() {
        String address = firstName;
        if(lastName != null && !lastName.isEmpty())  address += " " + lastName;

        if(!addressLine1.isEmpty()) address += ", " + addressLine1;

        if(addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;

        if(!city.isEmpty()) address += ", " + city;

        if(state != null && !state.isEmpty()) address += ", " + state;

        address += ", " + country.getName();

        if(!postalCode.isEmpty()) address += ". Postal code: " + postalCode;

        if(!phoneNumber.isEmpty()) address += ". Phone Number: " + phoneNumber;

        return address;
    }
}
