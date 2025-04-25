package com.ecommerce.exception;

public class PasswordsNotEqualException extends RuntimeException {
    public PasswordsNotEqualException(String message) {
        super(message);
    }
}
