package com.ecommerce.user;

import com.ecommerce.exception.PasswordsNotEqualException;
import com.ecommerce.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserValidator userValidator;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("john.doe@example.com")
                .password("encoded_password")
                .build();
    }

    @Test
    void validateUserDoesNotExist_ShouldDoNothing_WhenUserDoesNotExist() {
        // Arrange
        String email = "new.user@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> userValidator.validateUserDoesNotExist(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void validateUserDoesNotExist_ShouldThrowException_WhenUserExists() {
        // Arrange
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userValidator.validateUserDoesNotExist(email)
        );
        assertEquals("Uživatel s tímto e-mailem již existuje.", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void validatePasswordsEqual_ShouldDoNothing_WhenPasswordsMatch() {
        // Arrange
        String password = "password123";
        String confirmPassword = "password123";

        // Act & Assert
        assertDoesNotThrow(() -> userValidator.validatePasswordsEqual(password, confirmPassword));
    }

    @Test
    void validatePasswordsEqual_ShouldThrowException_WhenPasswordsDoNotMatch() {
        // Arrange
        String password = "password123";
        String confirmPassword = "password456";

        // Act & Assert
        PasswordsNotEqualException exception = assertThrows(
                PasswordsNotEqualException.class,
                () -> userValidator.validatePasswordsEqual(password, confirmPassword)
        );
        assertEquals("Zadaná hesla se neshodují.", exception.getMessage());
    }

    @Test
    void validateCurrentPasswordEquals_ShouldDoNothing_WhenPasswordMatches() {
        // Arrange
        String password = "password123";
        String storedPassword = "encoded_password";
        when(passwordEncoder.matches(password, storedPassword)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> userValidator.validateCurrentPasswordEquals(password, storedPassword));
        verify(passwordEncoder).matches(password, storedPassword);
    }

    @Test
    void validateCurrentPasswordEquals_ShouldThrowException_WhenPasswordDoesNotMatch() {
        // Arrange
        String password = "wrong_password";
        String storedPassword = "encoded_password";
        when(passwordEncoder.matches(password, storedPassword)).thenReturn(false);

        // Act & Assert
        PasswordsNotEqualException exception = assertThrows(
                PasswordsNotEqualException.class,
                () -> userValidator.validateCurrentPasswordEquals(password, storedPassword)
        );
        assertEquals("Aktuální heslo se neshoduje se zadaným heslem.", exception.getMessage());
        verify(passwordEncoder).matches(password, storedPassword);
    }
}