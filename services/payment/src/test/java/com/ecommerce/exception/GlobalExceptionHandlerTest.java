package com.ecommerce.exception;

import com.ecommerce.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private final String ERROR_MESSAGE = "Test error message";

    @BeforeEach
    void setUp() {
        // No additional setup needed as we're using Mockito annotations
    }

    @Test
    void handlePaymentMethodNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        PaymentMethodNotFoundException exception = new PaymentMethodNotFoundException(ERROR_MESSAGE);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handlePaymentMethodNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(ERROR_MESSAGE, problemDetail.getDetail());
    }

    @Test
    void handlePaymentNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        PaymentNotFoundException exception = new PaymentNotFoundException(ERROR_MESSAGE);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handlePaymentNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(ERROR_MESSAGE, problemDetail.getDetail());
    }

    @Test
    void handlePaymentMethodNotActiveException_ShouldReturnUnauthorizedStatus() {
        // Arrange
        PaymentMethodNotActiveException exception = new PaymentMethodNotActiveException(ERROR_MESSAGE);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handlePaymentMethodNotActiveException(exception);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED.value(), problemDetail.getStatus());
        assertEquals(ERROR_MESSAGE, problemDetail.getDetail());
    }

    @Test
    void handleUnsupportedOperationException_ShouldReturnBadRequestStatus() {
        // Arrange
        UnsupportedOperationException exception = new UnsupportedOperationException(ERROR_MESSAGE);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleUnsupportedOperationException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals(ERROR_MESSAGE, problemDetail.getDetail());
    }

    @Test
    void handleInvalidParameterException_ShouldReturnBadRequestStatus() {
        // Arrange
        InvalidParameterException exception = new InvalidParameterException(ERROR_MESSAGE);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleInvalidParameterException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals(ERROR_MESSAGE, problemDetail.getDetail());
    }

    @Test
    void handleNullPointerException_ShouldReturnInternalServerErrorStatus() {
        // Arrange
        NullPointerException exception = new NullPointerException(ERROR_MESSAGE);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleNullPointerException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals(ERROR_MESSAGE, problemDetail.getDetail());
    }

    @Test
    void handleExceptions_WithNullMessage_ShouldHandleGracefully() {
        // Arrange - Creating exceptions with null messages
        PaymentMethodNotFoundException paymentMethodNotFoundException = new PaymentMethodNotFoundException(null);
        PaymentNotFoundException paymentNotFoundException = new PaymentNotFoundException(null);
        PaymentMethodNotActiveException paymentMethodNotActiveException = new PaymentMethodNotActiveException(null);
        UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException((String) null);
        InvalidParameterException invalidParameterException = new InvalidParameterException((String) null);
        NullPointerException nullPointerException = new NullPointerException(null);

        // Act
        ProblemDetail detail1 = exceptionHandler.handlePaymentMethodNotFoundException(paymentMethodNotFoundException);
        ProblemDetail detail2 = exceptionHandler.handlePaymentNotFoundException(paymentNotFoundException);
        ProblemDetail detail3 = exceptionHandler.handlePaymentMethodNotActiveException(paymentMethodNotActiveException);
        ProblemDetail detail4 = exceptionHandler.handleUnsupportedOperationException(unsupportedOperationException);
        ProblemDetail detail5 = exceptionHandler.handleInvalidParameterException(invalidParameterException);
        ProblemDetail detail6 = exceptionHandler.handleNullPointerException(nullPointerException);

        // Assert - All methods should handle null messages without throwing exceptions
        assertNotNull(detail1);
        assertNotNull(detail2);
        assertNotNull(detail3);
        assertNotNull(detail4);
        assertNotNull(detail5);
        assertNotNull(detail6);
    }
}