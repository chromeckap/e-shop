package com.ecommerce.exception;

public class VariantNotFoundException extends RuntimeException {
    public VariantNotFoundException(String message) {
        super(message);
    }
}