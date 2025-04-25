package com.ecommerce.exception;

import com.ecommerce.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        // No additional setup needed as we're using @InjectMocks
    }

    @Test
    void handleOrderNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        String errorMessage = "Order with ID 123 not found";
        OrderNotFoundException exception = new OrderNotFoundException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleOrderNotFoundException(exception);

        // Assert
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problemDetail.getDetail()).isEqualTo(errorMessage);
    }

    @Test
    void handleUnauthorizedAccessException_ShouldReturnUnauthorizedStatus() {
        // Arrange
        String errorMessage = "User does not have permission to access this order";
        UnauthorizedAccessException exception = new UnauthorizedAccessException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleUnauthorizedAccessException(exception);

        // Assert
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(problemDetail.getDetail()).isEqualTo(errorMessage);
    }

    @Test
    void handleNullPointerException_ShouldReturnInternalServerErrorStatus() {
        // Arrange
        String errorMessage = "Unexpected null reference in order processing";
        NullPointerException exception = new NullPointerException(errorMessage);

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleNullPointerException(exception);

        // Assert
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getDetail()).isEqualTo(errorMessage);
    }

    @Test
    void handleNullPointerException_WithNullMessage_ShouldReturnInternalServerErrorStatus() {
        // Arrange
        NullPointerException exception = new NullPointerException();

        // Act
        ProblemDetail problemDetail = globalExceptionHandler.handleNullPointerException(exception);

        // Assert
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        // NullPointerException can have a null message, verify the handler handles this gracefully
        assertThat(problemDetail.getDetail()).isNull();
    }
}