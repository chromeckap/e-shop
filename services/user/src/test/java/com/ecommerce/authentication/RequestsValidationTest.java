package com.ecommerce.authentication;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RequestsValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("RegisterRequest Validation Tests")
    class RegisterRequestTests {

        @Test
        @DisplayName("Valid RegisterRequest should have no violations")
        void validRegisterRequest_ShouldHaveNoViolations() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    "john.doe@example.com",
                    "password123",
                    "password123"
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertTrue(violations.isEmpty(), "A valid RegisterRequest should have no constraint violations");
        }

        @Test
        @DisplayName("RegisterRequest with blank firstName should have violation")
        void blankFirstName_ShouldHaveViolation() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "",
                    "Doe",
                    "john.doe@example.com",
                    "password123",
                    "password123"
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(2, violations.size(), "Should have two violation");
        }

        @ParameterizedTest
        @DisplayName("RegisterRequest with firstName outside size limits should have violation")
        @ValueSource(strings = {"A", "ThisIsAReallyLongFirstNameThatExceedsFiftyCharactersLimit"})
        void firstNameSizeLimits_ShouldHaveViolation(String firstName) {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    firstName,
                    "Doe",
                    "john.doe@example.com",
                    "password123",
                    "password123"
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(1, violations.size(), "Should have one violation");
        }

        @Test
        @DisplayName("RegisterRequest with blank lastName should have violation")
        void blankLastName_ShouldHaveViolation() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "John",
                    "",
                    "john.doe@example.com",
                    "password123",
                    "password123"
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(2, violations.size(), "Should have two violation");
        }

        @ParameterizedTest
        @DisplayName("RegisterRequest with lastName outside size limits should have violation")
        @ValueSource(strings = {"A", "ThisIsAReallyLongLastNameThatExceedsFiftyCharactersLimit"})
        void lastNameSizeLimits_ShouldHaveViolation(String lastName) {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "John",
                    lastName,
                    "john.doe@example.com",
                    "password123",
                    "password123"
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(1, violations.size(), "Should have one violation");
        }

        @Test
        @DisplayName("RegisterRequest with blank email should have violation")
        void blankEmail_ShouldHaveViolation() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    "",
                    "password123",
                    "password123"
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(2, violations.size(), "Should have two violation");
        }

        @ParameterizedTest
        @DisplayName("RegisterRequest with invalid email format should have violation")
        @ValueSource(strings = {"johndoe", "johndoe@", "@example.com", "john@doe@example.com", "john doe@example.com"})
        void invalidEmailFormat_ShouldHaveViolation(String email) {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    email,
                    "password123",
                    "password123"
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(1, violations.size(), "Should have one violation");
        }

        @Test
        @DisplayName("RegisterRequest with blank password should have violation")
        void blankPassword_ShouldHaveViolation() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    "john.doe@example.com",
                    "",
                    "password123"
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(2, violations.size(), "Should have two violation");
        }

        @ParameterizedTest
        @DisplayName("RegisterRequest with too short password should have violation")
        @ValueSource(strings = {"pass"})
        void passwordTooShort_ShouldHaveViolation(String password) {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    "john.doe@example.com",
                    password,
                    password
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(2, violations.size(), "Should have two violation");
        }

        @Test
        @DisplayName("RegisterRequest with too long password should have violation")
        void passwordTooLong_ShouldHaveViolation() {
            // Arrange
            String longPassword = "a".repeat(256);
            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    "john.doe@example.com",
                    longPassword,
                    longPassword
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(2, violations.size(), "Should have two violation");
        }

        @Test
        @DisplayName("RegisterRequest with blank confirmPassword should have violation")
        void blankConfirmPassword_ShouldHaveViolation() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    "john.doe@example.com",
                    "password123",
                    ""
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(2, violations.size(), "Should have two violation");
        }

        @ParameterizedTest
        @DisplayName("RegisterRequest with too short confirmPassword should have violation")
        @ValueSource(strings = {"pass"})
        void confirmPasswordTooShort_ShouldHaveViolation(String confirmPassword) {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    "john.doe@example.com",
                    "password123",
                    confirmPassword
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(1, violations.size(), "Should have one violation");
        }

        @Test
        @DisplayName("RegisterRequest with too long confirmPassword should have violation")
        void confirmPasswordTooLong_ShouldHaveViolation() {
            // Arrange
            String longConfirmPassword = "a".repeat(256);
            RegisterRequest request = new RegisterRequest(
                    "John",
                    "Doe",
                    "john.doe@example.com",
                    "password123",
                    longConfirmPassword
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(1, violations.size(), "Should have one violation");
            assertEquals("Potvrení hesla musí mít 6 až 255 znaků.", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("RegisterRequest with multiple violations should report all violations")
        void multipleViolations_ShouldReportAll() {
            // Arrange
            RegisterRequest request = new RegisterRequest(
                    "",
                    "",
                    "invalid-email",
                    "",
                    ""
            );

            // Act
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(9, violations.size(), "Should have nine violations");
        }
    }

    @Nested
    @DisplayName("LoginRequest Validation Tests")
    class LoginRequestTests {

        @Test
        @DisplayName("Valid LoginRequest should have no violations")
        void validLoginRequest_ShouldHaveNoViolations() {
            // Arrange
            LoginRequest request = new LoginRequest(
                    "john.doe@example.com",
                    "password123"
            );

            // Act
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // Assert
            assertTrue(violations.isEmpty(), "A valid LoginRequest should have no constraint violations");
        }

        @Test
        @DisplayName("LoginRequest with blank email should have violation")
        void blankEmail_ShouldHaveViolation() {
            // Arrange
            LoginRequest request = new LoginRequest(
                    "",
                    "password123"
            );

            // Act
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(2, violations.size(), "Should have two violation");
        }

        @ParameterizedTest
        @DisplayName("LoginRequest with invalid email format should have violation")
        @ValueSource(strings = {"johndoe", "johndoe@", "@example.com", "john@doe@example.com", "john doe@example.com"})
        void invalidEmailFormat_ShouldHaveViolation(String email) {
            // Arrange
            LoginRequest request = new LoginRequest(
                    email,
                    "password123"
            );

            // Act
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(1, violations.size(), "Should have one violation");
            assertEquals("Neplatný formát emailu.", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("LoginRequest with blank password should have violation")
        void blankPassword_ShouldHaveViolation() {
            // Arrange
            LoginRequest request = new LoginRequest(
                    "john.doe@example.com",
                    ""
            );

            // Act
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(1, violations.size(), "Should have one violation");
            assertEquals("Heslo nesmí být prázdné.", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("LoginRequest with multiple violations should report all violations")
        void multipleViolations_ShouldReportAll() {
            // Arrange
            LoginRequest request = new LoginRequest(
                    "",
                    ""
            );

            // Act
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // Assert
            assertEquals(3, violations.size(), "Should have three violations");
        }
    }
}