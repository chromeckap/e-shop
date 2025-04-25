package com.ecommerce.exception;

import com.ecommerce.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleReviewExistsException() {
        // Arrange
        String errorMessage = "Review already exists for this product";
        ReviewExistsException exception = new ReviewExistsException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleReviewExistsException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleReviewNotFoundException() {
        // Arrange
        String errorMessage = "Review not found with given ID";
        ReviewNotFoundException exception = new ReviewNotFoundException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleReviewNotFoundException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleUnauthorizedAccessException() {
        // Arrange
        String errorMessage = "User does not have permission to access this resource";
        UnauthorizedAccessException exception = new UnauthorizedAccessException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleUnauthorizedAccessException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleNullPointerException() {
        // Arrange
        String errorMessage = "Null pointer encountered during processing";
        NullPointerException exception = new NullPointerException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleNullPointerException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void validateExceptionHandlerAnnotations() {
        // Verify that each exception handler method is annotated with @ExceptionHandler
        Arrays.stream(GlobalExceptionHandler.class.getDeclaredMethods())
                .filter(method -> method.getName().startsWith("handle"))
                .forEach(method -> {
                    assertTrue(method.isAnnotationPresent(ExceptionHandler.class),
                            "Method " + method.getName() + " should be annotated with @ExceptionHandler");

                    ExceptionHandler annotation = method.getAnnotation(ExceptionHandler.class);
                    assertNotNull(annotation.value(), "ExceptionHandler should specify an exception class");
                });
    }
}