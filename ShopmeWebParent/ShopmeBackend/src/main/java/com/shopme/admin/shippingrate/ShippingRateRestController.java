package com.shopme.admin.shippingrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingRateRestController {
     private final ShippingRateService shippingRateService;

     @Autowired
    public ShippingRateRestController(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @PostMapping("/get_shipping_cost")
    public String getShippingCost(
            @RequestParam(name = "productId") Integer productId,
            @RequestParam(name = "countryId") Integer countryId,
            @RequestParam(name = "state") String state
    ) throws ShippingRateNotFoundException
    {
        float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);
        return String.valueOf(shippingCost);
    }

}
