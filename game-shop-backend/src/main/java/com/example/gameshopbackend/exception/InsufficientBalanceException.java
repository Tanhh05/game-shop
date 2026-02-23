package com.example.gameshopbackend.exception;

/**
 * Exception khi số dư ví không đủ
 */
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

