package com.ecommerce.exception;

import com.ecommerce.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.nio.file.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleUserAlreadyExistsException_ShouldReturnBadRequestStatus() {
        // Arrange
        String errorMessage = "User with email test@example.com already exists";
        UserAlreadyExistsException exception = new UserAlreadyExistsException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleUserAlreadyExistsException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handlePasswordsNotEqualException_ShouldReturnBadRequestStatus() {
        // Arrange
        String errorMessage = "Passwords do not match";
        PasswordsNotEqualException exception = new PasswordsNotEqualException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handlePasswordsNotEqualException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleBadCredentialsException_ShouldReturnUnauthorizedStatus() {
        // Act
        ProblemDetail problemDetail = exceptionHandler.handleBadCredentialsException();

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), problemDetail.getStatus());
        assertEquals("Špatně zadané přihlašovací údaje.", problemDetail.getDetail());
    }

    @Test
    void handleUnauthorizedAccessException_ShouldReturnUnauthorizedStatus() {
        // Arrange
        String errorMessage = "Unauthorized access to resource";
        UnauthorizedAccessException exception = new UnauthorizedAccessException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleUnauthorizedAccessException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleNullPointerException_ShouldReturnInternalServerErrorStatus() {
        // Arrange
        String errorMessage = "Null reference encountered";
        NullPointerException exception = new NullPointerException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleNullPointerException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals(errorMessage, problemDetail.getDetail());
    }

    @Test
    void handleAccessDeniedException_ShouldReturnForbiddenStatus() {
        // Arrange
        String errorMessage = "Access denied to protected resource";
        AccessDeniedException exception = new AccessDeniedException(errorMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleAccessDeniedException(exception);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.FORBIDDEN.value(), problemDetail.getStatus());
        assertEquals("Nemáš dostatečné oprávnění.", problemDetail.getDetail());
    }
}