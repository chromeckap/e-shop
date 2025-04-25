package com.ecommerce.exception;

public class DeliveryMethodNotActiveException extends RuntimeException {
    public DeliveryMethodNotActiveException(String message) {
        super(message);
    }
}

