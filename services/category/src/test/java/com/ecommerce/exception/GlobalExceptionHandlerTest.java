package com.ecommerce.exception;

import com.ecommerce.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleCategoryNotFoundException_ReturnsNotFoundStatus() {
        // Arrange
        String errorMessage = "Category with ID 1 not found";
        CategoryNotFoundException exception = new CategoryNotFoundException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleCategoryNotFoundException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleInfiniteLoopException_ReturnsBadRequestStatus() {
        // Arrange
        String errorMessage = "Infinite loop detected in category hierarchy";
        InfiniteLoopException exception = new InfiniteLoopException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleInfiniteLoopException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleNullPointerException_ReturnsInternalServerErrorStatus() {
        // Arrange
        String errorMessage = "Null reference occurred";
        NullPointerException exception = new NullPointerException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleNullPointerException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleNullPointerException_WithNullMessage_HandlesCorrectly() {
        // Arrange - NullPointerException může mít null zprávu
        NullPointerException exception = new NullPointerException();

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleNullPointerException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        // Message může být null nebo prázdný, jen ověřujeme, že nedojde k chybě
    }
}