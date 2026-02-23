package com.example.gameshopbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaypalService {

    private final PaypalTokenService tokenService;
    private final RestTemplate restTemplate = new RestTemplate();

    // configurable via application.properties, with sensible defaults
    @Value("${paypal.base-url:https://api-m.sandbox.paypal.com}")
    private String paypalBaseUrl;

    @Value("${paypal.currency:USD}")
    private String currency;

    @Value("${paypal.exchange-rate:24000}")
    private double exchangeRate;

    public Map<String, Object> createOrder(Long amountVnd) {
        // validate input
        if (amountVnd == null || amountVnd <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        try {
            String token = tokenService.getAccessToken();
            String usdValue = convertVndToUsd(amountVnd);

            Map<String, Object> amountMap = Map.of(
                    "currency_code", currency,
                    "value", usdValue
            );

            Map<String, Object> purchaseUnit = Map.of("amount", amountMap);

            Map<String, Object> payload = Map.of(
                    "intent", "CAPTURE",
                    "purchase_units", List.of(purchaseUnit)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    paypalBaseUrl + "/v2/checkout/orders",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null) throw new IllegalStateException("Empty response from PayPal");

            // extract approve URL if present
            String approveUrl = null;
            Object linksObj = body.get("links");
            if (linksObj instanceof List) {
                for (Object o : (List<?>) linksObj) {
                    if (o instanceof Map) {
                        Object rel = ((Map<?, ?>) o).get("rel");
                        if ("approve".equals(rel)) {
                            approveUrl = Objects.toString(((Map<?, ?>) o).get("href"), null);
                            break;
                        }
                    }
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", body.get("id"));
            result.put("status", body.get("status"));
            result.put("approveUrl", approveUrl);
            result.put("raw", body);
            // echo back original amount (VND) to help frontend mapping
            result.put("amountVnd", amountVnd);
            return result;
        } catch (RestClientException ex) {
            throw new IllegalStateException("PayPal create order failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Capture order and return true only when capture is COMPLETED.
     */
    public boolean captureOrder(String orderId) {
        // validate input
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId is required");
        }

        try {
            String token = tokenService.getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of(), headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    paypalBaseUrl + "/v2/checkout/orders/" + orderId + "/capture",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null) return false;

            // try to parse captures under purchase_units -> payments -> captures
            try {
                Object pus = body.get("purchase_units");
                if (pus instanceof List && !((List<?>) pus).isEmpty()) {
                    Object firstPu = ((List<?>) pus).get(0);
                    if (firstPu instanceof Map) {
                        Object payments = ((Map<?, ?>) firstPu).get("payments");
                        if (payments instanceof Map) {
                            Object captures = ((Map<?, ?>) payments).get("captures");
                            if (captures instanceof List && !((List<?>) captures).isEmpty()) {
                                Object firstCapture = ((List<?>) captures).get(0);
                                if (firstCapture instanceof Map) {
                                    String status = Objects.toString(((Map<?, ?>) firstCapture).get("status"), "");
                                    return "COMPLETED".equalsIgnoreCase(status);
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignore) {
                // fallback to top-level status
            }

            String topStatus = Objects.toString(body.get("status"), "");
            return "COMPLETED".equalsIgnoreCase(topStatus);
        } catch (RestClientException ex) {
            throw new IllegalStateException("PayPal capture failed: " + ex.getMessage(), ex);
        }
    }

    private String convertVndToUsd(Long vnd) {
        if (vnd == null) return "0.00";
        BigDecimal vndBd = BigDecimal.valueOf(vnd);
        BigDecimal rate = BigDecimal.valueOf(exchangeRate > 0 ? exchangeRate : 24000.0);
        BigDecimal usd = vndBd.divide(rate, 2, RoundingMode.HALF_UP);
        return usd.toPlainString();
    }

}
