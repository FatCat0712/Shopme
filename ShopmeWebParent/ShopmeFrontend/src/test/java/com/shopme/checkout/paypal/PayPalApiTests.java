package com.shopme.checkout.paypal;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class PayPalApiTests {
    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static final String CLIENT_ID = "AZb9q-xIQxAKTQS1XOuhecvxCXIHaqa08KlJWmvf1rnTdKjiTeSnyqEAa7mx00q1vr_h5Nx7p1hJqX7B";
    private static final String CLIENT_SECRET = "EETu6ZHLXtONwP_HqbSyVsAjqORKQscokK0S0fczPP-yEgvyigHN0sWcXpFZ64bK0Z4hyS-Ls8OlWQnl";

    @Test
    public void testGetOrderDetails() {
        String orderId = "26M64030SJ865642V";
        String requestURL = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PaypalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request, PaypalOrderResponse.class);

        PaypalOrderResponse orderResponse = (PaypalOrderResponse)response.getBody();

        System.out.println("Order ID: " + orderResponse.getId());
        System.out.println("Validated: " + orderResponse.validate(orderId));
    }
}
