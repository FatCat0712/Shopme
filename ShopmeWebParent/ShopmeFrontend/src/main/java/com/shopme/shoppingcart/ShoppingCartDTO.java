package com.shopme.shoppingcart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingCartDTO {
    private int quantity;
    private String message;

    public ShoppingCartDTO(int quantity, String message) {
        this.quantity = quantity;
        this.message = message;
    }

    public ShoppingCartDTO(String message) {
        this.message = message;
    }
}
