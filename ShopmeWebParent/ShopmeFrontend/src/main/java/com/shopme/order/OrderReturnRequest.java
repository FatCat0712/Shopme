package com.shopme.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderReturnRequest {
    private Integer orderId;
    private String reason;
    private String note;

    public OrderReturnRequest() {
    }

    public OrderReturnRequest(Integer orderId, String reason, String note) {
        this.orderId = orderId;
        this.reason = reason;
        this.note = note;
    }
}
