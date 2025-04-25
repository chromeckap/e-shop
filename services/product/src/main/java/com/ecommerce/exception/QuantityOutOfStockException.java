package com.ecommerce.exception;

public class QuantityOutOfStockException extends RuntimeException {
    public QuantityOutOfStockException(String message) {
        super(message);
    }
}