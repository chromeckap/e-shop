package com.ecommerce.exception;

public class AttributeValueNotFoundException extends RuntimeException {
    public AttributeValueNotFoundException(String message) {
        super(message);
    }
}