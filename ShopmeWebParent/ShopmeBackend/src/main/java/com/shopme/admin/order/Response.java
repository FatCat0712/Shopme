package com.shopme.admin.order;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response {
    public Response(Integer orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }
    private Integer orderId;
    private String status;
}
