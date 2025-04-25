package com.ecommerce.exception.handler;

import com.ecommerce.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AttributeNotFoundException.class)
    public ProblemDetail handleAttributeNotFoundException(AttributeNotFoundException exception) {
        log.error("Attribute not found: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(AttributeValueNotFoundException.class)
    public ProblemDetail handleAttributeValueNotFoundException(AttributeValueNotFoundException exception) {
        log.error("Attribute value not found: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleProductNotFoundException(ProductNotFoundException exception) {
        log.error("Product not found: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(QuantityOutOfStockException.class)
    public ProblemDetail handleQuantityOutOfStockException(QuantityOutOfStockException exception) {
        log.error("Quantity out of stock: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(SelfRelatingProductException.class)
    public ProblemDetail handleSelfRelatingProductException(SelfRelatingProductException exception) {
        log.error("Self-relating product error: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(VariantNotFoundException.class)
    public ProblemDetail handleVariantNotFoundException(VariantNotFoundException exception) {
        log.error("Variant not found: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ProblemDetail handleNullPointerException(NullPointerException exception) {
        log.error("Null pointer error: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

}
