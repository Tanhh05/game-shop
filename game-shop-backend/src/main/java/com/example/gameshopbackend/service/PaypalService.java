package com.example.gameshopbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaypalService {

    private final PaypalTokenService tokenService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${paypal.base-url:https://api-m.sandbox.paypal.com}")
    private String paypalBaseUrl;

    @Value("${paypal.currency:USD}")
    private String currency;

    @Value("${paypal.exchange-rate:24000}")
    private double exchangeRate;

    /* ================= CREATE ORDER ================= */

    public Map<String, Object> createOrder(Long amountVnd) {

        if (amountVnd == null || amountVnd <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        String token = tokenService.getAccessToken();
        String usdValue = convertVndToUsd(amountVnd);

        Map<String, Object> payload = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(
                        Map.of("amount",
                                Map.of(
                                        "currency_code", currency,
                                        "value", usdValue
                                )
                        )
                ),
                "application_context", Map.of(
                        "return_url", "http://localhost:5173/payment-success",
                        "cancel_url", "http://localhost:5173/payment-cancel"
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = restTemplate.exchange(
                paypalBaseUrl + "/v2/checkout/orders",
                HttpMethod.POST,
                new HttpEntity<>(payload, headers),
                Map.class
        );

        Map<String, Object> body = response.getBody();

        String approveUrl = null;
        List<?> links = (List<?>) body.get("links");

        for (Object o : links) {
            Map<?, ?> link = (Map<?, ?>) o;
            if ("approve".equals(link.get("rel"))) {
                approveUrl = link.get("href").toString();
            }
        }

        return Map.of(
                "orderId", body.get("id"),
                "approveUrl", approveUrl
        );
    }

    /* ================= CAPTURE ORDER ================= */

    public BigDecimal captureOrder(String orderId) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId is required");
        }

        try {
            String token = tokenService.getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Map> response = restTemplate.exchange(
                    paypalBaseUrl + "/v2/checkout/orders/" + orderId + "/capture",
                    HttpMethod.POST,
                    new HttpEntity<>(Map.of(), headers),
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null) return null;

            List<?> purchaseUnits = (List<?>) body.get("purchase_units");
            if (purchaseUnits == null || purchaseUnits.isEmpty()) return null;

            Map<?, ?> firstPu = (Map<?, ?>) purchaseUnits.get(0);
            Map<?, ?> payments = (Map<?, ?>) firstPu.get("payments");
            List<?> captures = (List<?>) payments.get("captures");

            if (captures == null || captures.isEmpty()) return null;

            Map<?, ?> firstCapture = (Map<?, ?>) captures.get(0);

            String status = Objects.toString(firstCapture.get("status"), "");
            if (!"COMPLETED".equalsIgnoreCase(status)) return null;

            Map<?, ?> amount = (Map<?, ?>) firstCapture.get("amount");
            String value = Objects.toString(amount.get("value"), "0");

            return new BigDecimal(value);

        } catch (RestClientException ex) {
            throw new IllegalStateException("PayPal capture failed: " + ex.getMessage(), ex);
        }
    }

    /* ================= CONVERT ================= */

    public long convertUsdToVnd(BigDecimal usd) {

        if (usd == null) return 0;

        BigDecimal rate = BigDecimal.valueOf(exchangeRate);
        return usd.multiply(rate).longValue();
    }

    private String convertVndToUsd(Long vnd) {

        BigDecimal rate = BigDecimal.valueOf(exchangeRate);
        BigDecimal usd = BigDecimal.valueOf(vnd)
                .divide(rate, 2, RoundingMode.HALF_UP);

        return usd.toPlainString();
    }
}