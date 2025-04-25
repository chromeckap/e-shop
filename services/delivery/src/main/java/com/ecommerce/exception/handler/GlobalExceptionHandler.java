package com.ecommerce.exception.handler;

import com.ecommerce.exception.DeliveryMethodNotActiveException;
import com.ecommerce.exception.DeliveryMethodNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DeliveryMethodNotFoundException.class)
    public ProblemDetail handleDeliveryMethodNotFoundException(DeliveryMethodNotFoundException exception) {
        log.error("Delivery Method not found: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DeliveryMethodNotActiveException.class)
    public ProblemDetail handleDeliveryMethodNotActiveException(DeliveryMethodNotActiveException exception) {
        log.error("Delivery Method not active: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ProblemDetail handleNullPointerException(NullPointerException exception) {
        log.error("Null pointer error: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

}
