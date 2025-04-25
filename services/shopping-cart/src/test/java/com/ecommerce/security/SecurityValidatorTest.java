package com.ecommerce.security;

import com.ecommerce.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityValidatorTest {

    @InjectMocks
    private SecurityValidator securityValidator;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    @Test
    @DisplayName("Should validate successfully when userId matches authenticated user")
    void validateUserAccess_ShouldValidateSuccessfully() {
        // Arrange
        Long userId = 123L;
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("123");

        // Act & Assert
        assertDoesNotThrow(() -> securityValidator.validateUserAccess(userId));
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when authentication is null")
    void validateUserAccess_ShouldThrowExceptionWhenAuthenticationIsNull() {
        // Arrange
        Long userId = 123L;
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> securityValidator.validateUserAccess(userId)
        );
        assertEquals("Uživatel není autentizován.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when user is not authenticated")
    void validateUserAccess_ShouldThrowExceptionWhenUserIsNotAuthenticated() {
        // Arrange
        Long userId = 123L;
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> securityValidator.validateUserAccess(userId)
        );
        assertEquals("Uživatel není autentizován.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when user ID format is invalid")
    void validateUserAccess_ShouldThrowExceptionWhenUserIdFormatIsInvalid() {
        // Arrange
        Long userId = 123L;
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("invalid-id");

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> securityValidator.validateUserAccess(userId)
        );
        assertEquals("Neplatný formát ID uživatele.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when user IDs don't match")
    void validateUserAccess_ShouldThrowExceptionWhenUserIdsDontMatch() {
        // Arrange
        Long userId = 123L;
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("456");

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> securityValidator.validateUserAccess(userId)
        );
        assertEquals("Uživatel může přistupovat pouze k vlastnímu košíku.", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate successfully with real Authentication object")
    void validateUserAccess_ShouldValidateSuccessfullyWithRealAuthentication() {
        // Arrange
        Long userId = 123L;
        Authentication realAuth = new UsernamePasswordAuthenticationToken(
                "123",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(securityContext.getAuthentication()).thenReturn(realAuth);

        // Act & Assert
        assertDoesNotThrow(() -> securityValidator.validateUserAccess(userId));
    }

    @Test
    @DisplayName("Should validate null userId with matching authenticated user")
    void validateUserAccess_ShouldValidateNullUserId() {
        // Arrange
        Long userId = null;

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> securityValidator.validateUserAccess(userId));
    }
}