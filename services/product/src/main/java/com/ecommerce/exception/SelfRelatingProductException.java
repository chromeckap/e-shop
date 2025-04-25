package com.ecommerce.exception;

public class SelfRelatingProductException extends RuntimeException {
    public SelfRelatingProductException(String message) {
        super(message);
    }
}