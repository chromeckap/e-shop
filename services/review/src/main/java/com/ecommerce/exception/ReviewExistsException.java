package com.ecommerce.exception;

public class ReviewExistsException extends RuntimeException {
    public ReviewExistsException(String message) {
        super(message);
    }
}