package com.ecommerce.exception;

import com.ecommerce.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleAttributeNotFoundException_ReturnsNotFoundStatus() {
        // Arrange
        String errorMessage = "Attribute with ID 1 not found";
        AttributeNotFoundException exception = new AttributeNotFoundException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleAttributeNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleAttributeValueNotFoundException_ReturnsNotFoundStatus() {
        // Arrange
        String errorMessage = "Attribute value with ID 1 not found";
        AttributeValueNotFoundException exception = new AttributeValueNotFoundException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleAttributeValueNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleProductNotFoundException_ReturnsNotFoundStatus() {
        // Arrange
        String errorMessage = "Product with ID 1 not found";
        ProductNotFoundException exception = new ProductNotFoundException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleProductNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleQuantityOutOfStockException_ReturnsConflictStatus() {
        // Arrange
        String errorMessage = "Requested quantity exceeds available stock";
        QuantityOutOfStockException exception = new QuantityOutOfStockException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleQuantityOutOfStockException(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleSelfRelatingProductException_ReturnsBadRequestStatus() {
        // Arrange
        String errorMessage = "Product cannot relate to itself";
        SelfRelatingProductException exception = new SelfRelatingProductException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleSelfRelatingProductException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleVariantNotFoundException_ReturnsNotFoundStatus() {
        // Arrange
        String errorMessage = "Variant with ID 1 not found";
        VariantNotFoundException exception = new VariantNotFoundException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleVariantNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleNullPointerException_ReturnsInternalServerErrorStatus() {
        // Arrange
        String errorMessage = "Unexpected null value";
        NullPointerException exception = new NullPointerException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleNullPointerException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleNullPointerException_WithNullMessage_HandlesGracefully() {
        // Arrange
        NullPointerException exception = new NullPointerException();

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleNullPointerException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
    }
}