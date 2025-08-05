package com.shopme.common.entity.order;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.IdBasedEntity;
import com.shopme.common.entity.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    public OrderDetail() {
    }

    public OrderDetail(String categoryName, int quantity, float productCost, float shippingCost, float subTotal) {
        this.product = new Product();
        this.product.setCategory(new Category(categoryName));
        this.quantity = quantity;
        this.productCost = productCost;
        this.shippingCost = shippingCost;
        this.subTotal = subTotal;
    }

    public OrderDetail(int quantity, String productName, float productCost, float shippingCost, float Subtotal) {
        this.product = new Product();
        this.product.setName(productName);
        this.quantity = quantity;
        this.productCost = productCost;
        this.shippingCost = shippingCost;
    }

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;


}
