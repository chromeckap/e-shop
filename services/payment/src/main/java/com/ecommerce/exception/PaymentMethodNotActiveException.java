package com.ecommerce.exception;

public class PaymentMethodNotActiveException extends RuntimeException {
    public PaymentMethodNotActiveException(String message) {
        super(message);
    }
}

