package com.shopme.common.entity;

import com.shopme.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItem extends IdBasedEntity{

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    @Transient
    private float shippingCost;

    public CartItem() {
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", customer=" + customer.getFullName() +
                ", product=" + product.getShortName() +
                ", quantity=" + quantity +
                '}';
    }

    @Transient
    public float getSubTotal() {
        return product.getDiscountPrice() * quantity;
    }

    @Transient
    public float getShippingCost() {
        return shippingCost;
    }


}
