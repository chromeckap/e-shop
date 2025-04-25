package com.ecommerce.exception;

public class InfiniteLoopException extends RuntimeException {
    public InfiniteLoopException(String message) {
        super(message);
    }
}
