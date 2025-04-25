package com.ecommerce.exception;

public class DeliveryMethodNotFoundException extends RuntimeException {
    public DeliveryMethodNotFoundException(String message) {
        super(message);
    }
}
