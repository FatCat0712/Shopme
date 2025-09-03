package com.shopme.admin.shippingrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ShippingRateRestController {
     private final ShippingRateService shippingRateService;

     @Autowired
    public ShippingRateRestController(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @PostMapping("/get_shipping_cost")
    public ResponseEntity<?> getShippingCost(
            @RequestParam(name = "productId") Integer productId,
            @RequestParam(name = "countryId") Integer countryId,
            @RequestParam(name = "state") String state
    )
    {
        float shippingCost = 0;
        try {
            shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);
            return ResponseEntity.ok(shippingCost);
        } catch (ShippingRateNotFoundException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }

    }

}
