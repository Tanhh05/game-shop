package com.example.gameshopbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SePayWebhookController {

    @PostMapping("/webhook/sepay")
    public ResponseEntity<?> webhook(@RequestBody Map
            <String, Object> payload) {

        System.out.println("SePay payload: " + payload);

        return ResponseEntity.ok("OK");
    }
}