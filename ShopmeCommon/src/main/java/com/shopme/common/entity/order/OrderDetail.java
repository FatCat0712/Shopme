package com.shopme.common.entity.order;

import com.shopme.common.entity.IdBasedEntity;
import com.shopme.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_details")
@Getter
@Setter
public class OrderDetail extends IdBasedEntity {

    private int quantity;
    private float productCost;
    private float shippingCost;
    private float unitPrice;
    private float subTotal;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;


}
