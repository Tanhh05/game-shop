package com.example.gameshopbackend.service;

import com.example.gameshopbackend.config.PaypalProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaypalTokenService {

    private final PaypalProperties paypalProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(
                paypalProperties.getClientId(),
                paypalProperties.getClientSecret()
        );
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity =
                new HttpEntity<>("grant_type=client_credentials", headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                paypalProperties.getBaseUrl() + "/v1/oauth2/token",
                HttpMethod.POST,
                entity,
                Map.class
        );

        return response.getBody().get("access_token").toString();
    }
}
