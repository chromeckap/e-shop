package com.ecommerce.exception;

import com.ecommerce.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private static final String ERROR_MESSAGE = "Test error message";

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleDeliveryMethodNotFoundException() {
        // Arrange
        DeliveryMethodNotFoundException exception = new DeliveryMethodNotFoundException(ERROR_MESSAGE);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleDeliveryMethodNotFoundException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(ERROR_MESSAGE, problemDetail.getDetail());
    }

    @Test
    void testHandleDeliveryMethodNotActiveException() {
        // Arrange
        DeliveryMethodNotActiveException exception = new DeliveryMethodNotActiveException(ERROR_MESSAGE);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleDeliveryMethodNotActiveException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(ERROR_MESSAGE, problemDetail.getDetail());
    }

    @Test
    void testHandleNullPointerException() {
        // Arrange
        NullPointerException exception = new NullPointerException(ERROR_MESSAGE);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleNullPointerException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals(ERROR_MESSAGE, problemDetail.getDetail());
    }

    @Test
    void testExceptionHandlerAnnotations() {
        // Verify ExceptionHandler annotations are present on the methods
        try {
            assertTrue(GlobalExceptionHandler.class.getMethod("handleDeliveryMethodNotFoundException", DeliveryMethodNotFoundException.class)
                    .isAnnotationPresent(org.springframework.web.bind.annotation.ExceptionHandler.class));

            assertTrue(GlobalExceptionHandler.class.getMethod("handleDeliveryMethodNotActiveException", DeliveryMethodNotActiveException.class)
                    .isAnnotationPresent(org.springframework.web.bind.annotation.ExceptionHandler.class));

            assertTrue(GlobalExceptionHandler.class.getMethod("handleNullPointerException", NullPointerException.class)
                    .isAnnotationPresent(org.springframework.web.bind.annotation.ExceptionHandler.class));
        } catch (NoSuchMethodException e) {
            fail("Method not found", e);
        }

        // Verify class-level annotations
        assertTrue(GlobalExceptionHandler.class.isAnnotationPresent(org.springframework.web.bind.annotation.RestControllerAdvice.class));
    }

    @Test
    void testLoggingBehavior() {
        // This test verifies that logging methods are called without throwing exceptions
        // For this, we'd typically use a mocking framework to verify logging
        // Note: This is a simplified approach and might need more sophisticated logging verification
        DeliveryMethodNotFoundException notFoundException = new DeliveryMethodNotFoundException(ERROR_MESSAGE);
        DeliveryMethodNotActiveException notActiveException = new DeliveryMethodNotActiveException(ERROR_MESSAGE);
        NullPointerException nullPointerException = new NullPointerException(ERROR_MESSAGE);

        // These calls should not throw any exceptions
        exceptionHandler.handleDeliveryMethodNotFoundException(notFoundException);
        exceptionHandler.handleDeliveryMethodNotActiveException(notActiveException);
        exceptionHandler.handleNullPointerException(nullPointerException);
    }
}