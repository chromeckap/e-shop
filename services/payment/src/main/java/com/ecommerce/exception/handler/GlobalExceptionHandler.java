package com.ecommerce.exception.handler;

import com.ecommerce.exception.PaymentMethodNotActiveException;
import com.ecommerce.exception.PaymentMethodNotFoundException;
import com.ecommerce.exception.PaymentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.InvalidParameterException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ProblemDetail handlePaymentMethodNotFoundException(PaymentMethodNotFoundException exception) {
        log.error("Payment Method not found: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ProblemDetail handlePaymentNotFoundException(PaymentNotFoundException exception) {
        log.error("Payment not found: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(PaymentMethodNotActiveException.class)
    public ProblemDetail handlePaymentMethodNotActiveException(PaymentMethodNotActiveException exception) {
        log.error("Payment method is not active: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ProblemDetail handleUnsupportedOperationException(UnsupportedOperationException exception) {
        log.error("Unsupported operation: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ProblemDetail handleInvalidParameterException(InvalidParameterException exception) {
        log.error("Invalid parameter: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ProblemDetail handleNullPointerException(NullPointerException exception) {
        log.error("Null pointer error: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

}
