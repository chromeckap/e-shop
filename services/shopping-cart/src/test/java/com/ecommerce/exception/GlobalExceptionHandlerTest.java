package com.ecommerce.exception;

import com.ecommerce.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleCartItemNotFoundException_ReturnsCorrectProblemDetail() {
        // Arrange
        String errorMessage = "Cart item not found";
        CartItemNotFoundException exception = new CartItemNotFoundException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleCartItemNotFoundException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleIllegalArgumentException_ReturnsCorrectProblemDetail() {
        // Arrange
        String errorMessage = "Invalid quantity";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleUnauthorizedAccessException_ReturnsCorrectProblemDetail() {
        // Arrange
        String errorMessage = "User not authorized";
        UnauthorizedAccessException exception = new UnauthorizedAccessException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleUnauthorizedAccessException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleNullPointerException_ReturnsCorrectProblemDetail() {
        // Arrange
        String errorMessage = "Null pointer error occurred";
        NullPointerException exception = new NullPointerException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleNullPointerException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleAccessDeniedException_ReturnsCorrectProblemDetail() {
        // Arrange
        String errorMessage = "Access denied";
        AccessDeniedException exception = new AccessDeniedException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleAccessDeniedException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.FORBIDDEN.value(), problemDetail.getStatus());
        assertEquals("Nemáš dostatečné oprávnění.", problemDetail.getDetail());
    }

    @Test
    void validateExceptionHandlerAnnotations() {
        // Verify that each method is annotated with @ExceptionHandler
        var methods = GlobalExceptionHandler.class.getDeclaredMethods();

        for (var method : methods) {
            if (method.getName().startsWith("handle")) {
                assertTrue(method.isAnnotationPresent(ExceptionHandler.class),
                        "Method " + method.getName() + " should be annotated with @ExceptionHandler");

                var annotation = method.getAnnotation(ExceptionHandler.class);
                assertNotNull(annotation.value(), "ExceptionHandler should specify an exception class");
            }
        }
    }
}