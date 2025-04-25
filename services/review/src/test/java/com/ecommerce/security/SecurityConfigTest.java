package com.ecommerce.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private SecurityConfig securityConfig;

    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() throws Exception {
        authenticationManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
    }

    @Test
    void authenticationManager_ShouldReturnConfiguredManager() throws Exception {
        // Act
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        // Assert
        assertEquals(authenticationManager, result);
        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }

}