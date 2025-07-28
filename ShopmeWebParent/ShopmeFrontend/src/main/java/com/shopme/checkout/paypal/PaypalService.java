package com.shopme.checkout.paypal;

import com.shopme.setting.PaymentSettingBag;
import com.shopme.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PaypalService {
    private final SettingService settingService;

    @Autowired
    public PaypalService(SettingService settingService) {
        this.settingService = settingService;
    }

    public boolean validateOrder(String orderId) throws PayPalApiException {
        PaypalOrderResponse orderResponse = getOrderDetails(orderId);

        assert orderResponse != null;
        return orderResponse.validate(orderId);
    }

    private PaypalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
        ResponseEntity<PaypalOrderResponse> response = makeRequest(orderId);

        HttpStatus statusCode = (HttpStatus) response.getStatusCode();

        if(!statusCode.equals(HttpStatus.OK)) {
           throwExceptionForNonOKResponse(statusCode);
        }

        return response.getBody();
    }

    private ResponseEntity<PaypalOrderResponse> makeRequest(String orderId) {
        PaymentSettingBag paymentSettingBag = settingService.getPaymentSettings();
        String requestURL = paymentSettingBag.getURL() + "/v2/checkout/orders/" + orderId;
        String clientID = paymentSettingBag.getClientID();
        String clientSecret = paymentSettingBag.getClientSecret();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(clientID, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(requestURL, HttpMethod.GET, request, PaypalOrderResponse.class);
    }

    private void throwExceptionForNonOKResponse(HttpStatus statusCode) throws PayPalApiException {
        String message;
        switch (statusCode) {
            case NOT_FOUND -> message = "Order ID not found";
            case BAD_REQUEST -> message = "Bad Request to PayPal Checkout API";
            case INTERNAL_SERVER_ERROR -> message = "PayPal Server Error";
            default -> message = "PayPal returned non-OK status code";
        }
        throw new PayPalApiException(message);
    }
 }


