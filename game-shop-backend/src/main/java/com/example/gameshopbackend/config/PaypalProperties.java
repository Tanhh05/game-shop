package com.example.gameshopbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.paypal")
@Getter
@Setter
public class PaypalProperties {
    private String clientId;
    private String clientSecret;
    private String baseUrl;
}
