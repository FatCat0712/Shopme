package com.shopme.common.entity;

import com.shopme.common.entity.country.Country;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "shipping_rates")
@Getter
@Setter
public class ShippingRate extends IdBasedEntity{
    @Column(nullable = false)
    private Float rate;

    @Column(nullable = false)
    private Integer days;

    @Column(name="cod_supported", nullable = false)
    private Boolean codSupported;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(nullable = false, length = 45)
    private String state;

    @Override
    public String toString() {
        return "ShippingRate{" +
                "id=" + id +
                ", rate=" + rate +
                ", days=" + days +
                ", codSupported=" + codSupported +
                ", country=" + country.getName() +
                ", state='" + state + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShippingRate that = (ShippingRate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
