package com.shopme.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderReturnResponse {
    private Integer orderId;

    public OrderReturnResponse() {
    }

    public OrderReturnResponse(Integer orderId) {
        this.orderId = orderId;
    }
}
