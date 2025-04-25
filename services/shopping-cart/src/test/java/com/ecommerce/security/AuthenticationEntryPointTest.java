package com.ecommerce.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Test
    void commence_SetsUnauthorizedErrorResponse() throws IOException {
        // Act
        authenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response, times(1)).sendError(
                eq(HttpServletResponse.SC_UNAUTHORIZED),
                eq("Nemáš dostatečné oprávnění.")
        );
    }

    @Test
    void commence_WithIOException_HandlesException() throws IOException {
        // Arrange
        doThrow(new IOException("Test exception")).when(response).sendError(
                anyInt(), anyString()
        );

        // Act & Assert - should not propagate the exception
        assertThrows(IOException.class, () ->
                authenticationEntryPoint.commence(request, response, authException));
    }
}