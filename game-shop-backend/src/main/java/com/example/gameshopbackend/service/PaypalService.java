package com.example.gameshopbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaypalService {

    private final PaypalTokenService tokenService;
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> createOrder(Long amountVnd) {

        String token = tokenService.getAccessToken();

        Map<String, Object> payload = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(
                        Map.of(
                                "amount", Map.of(
                                        "currency_code", "USD",
                                        "value", convertVndToUsd(amountVnd)
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api-m.sandbox.paypal.com/v2/checkout/orders",
                HttpMethod.POST,
                entity,
                Map.class
        );

        return response.getBody();
    }

    public void captureOrder(String orderId) {

        String token = tokenService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(Map.of(), headers);

        restTemplate.exchange(
                "https://api-m.sandbox.paypal.com/v2/checkout/orders/"
                        + orderId + "/capture",
                HttpMethod.POST,
                entity,
                Map.class
        );
    }

    private String convertVndToUsd(Long vnd) {
        double usd = vnd / 24000.0;
        return String.format("%.2f", usd);
    }

}
