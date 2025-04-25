package com.ecommerce.config;

import com.ecommerce.user.User;
import com.ecommerce.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeansConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private User user;

    @InjectMocks
    private BeansConfig beansConfig;

    @Test
    void userDetailsService_ShouldReturnUserWhenFound() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetailsService userDetailsService = beansConfig.userDetailsService();
        Object result = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void userDetailsService_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        UserDetailsService userDetailsService = beansConfig.userDetailsService();

        // Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email)
        );
        assertEquals("Uživatel nebyl nalezen.", exception.getMessage());
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // Act
        PasswordEncoder passwordEncoder = beansConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }

    @Test
    void authenticationProvider_ShouldReturnConfiguredProvider() {
        // Act
        AuthenticationProvider provider = beansConfig.authenticationProvider();

        // Assert
        assertNotNull(provider);
        assertInstanceOf(DaoAuthenticationProvider.class, provider);

        // We can't directly access the internal state of DaoAuthenticationProvider
        // so we're mostly testing that it's created without errors
    }
}