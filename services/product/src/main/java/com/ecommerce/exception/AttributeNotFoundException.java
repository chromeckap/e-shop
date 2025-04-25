package com.ecommerce.exception;

public class AttributeNotFoundException extends RuntimeException {
    public AttributeNotFoundException(String message) {
        super(message);
    }
}