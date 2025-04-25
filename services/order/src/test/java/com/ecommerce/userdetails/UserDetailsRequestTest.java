package com.ecommerce.userdetails;

import com.ecommerce.address.AddressRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Should validate valid UserDetailsRequest")
    void shouldValidateValidUserDetailsRequest() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest request = new UserDetailsRequest(
                123L,
                "John",
                "Doe",
                "john.doe@example.com",
                addressRequest
        );

        // Act
        Set<ConstraintViolation<UserDetailsRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should invalidate UserDetailsRequest with null id")
    void shouldInvalidateUserDetailsRequestWithNullId() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest request = new UserDetailsRequest(
                null,
                "John",
                "Doe",
                "john.doe@example.com",
                addressRequest
        );

        // Act
        Set<ConstraintViolation<UserDetailsRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<UserDetailsRequest> violation = violations.iterator().next();
        assertEquals("ID uživatele musí být zadáno.", violation.getMessage());
        assertEquals("id", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should invalidate UserDetailsRequest with blank firstName")
    void shouldInvalidateUserDetailsRequestWithBlankFirstName() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest request = new UserDetailsRequest(
                123L,
                "",
                "Doe",
                "john.doe@example.com",
                addressRequest
        );

        // Act
        Set<ConstraintViolation<UserDetailsRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(2, violations.size());
        ConstraintViolation<UserDetailsRequest> violation = violations.iterator().next();
        assertEquals("firstName", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should invalidate UserDetailsRequest with firstName too short")
    void shouldInvalidateUserDetailsRequestWithFirstNameTooShort() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest request = new UserDetailsRequest(
                123L,
                "J",
                "Doe",
                "john.doe@example.com",
                addressRequest
        );

        // Act
        Set<ConstraintViolation<UserDetailsRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<UserDetailsRequest> violation = violations.iterator().next();
        assertEquals("Jméno musí mít 2 až 50 znaků.", violation.getMessage());
        assertEquals("firstName", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should invalidate UserDetailsRequest with firstName too long")
    void shouldInvalidateUserDetailsRequestWithFirstNameTooLong() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        String longName = "J".repeat(51);

        UserDetailsRequest request = new UserDetailsRequest(
                123L,
                longName,
                "Doe",
                "john.doe@example.com",
                addressRequest
        );

        // Act
        Set<ConstraintViolation<UserDetailsRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<UserDetailsRequest> violation = violations.iterator().next();
        assertEquals("Jméno musí mít 2 až 50 znaků.", violation.getMessage());
        assertEquals("firstName", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should invalidate UserDetailsRequest with blank lastName")
    void shouldInvalidateUserDetailsRequestWithBlankLastName() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest request = new UserDetailsRequest(
                123L,
                "John",
                "",
                "john.doe@example.com",
                addressRequest
        );

        // Act
        Set<ConstraintViolation<UserDetailsRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(2, violations.size());
        ConstraintViolation<UserDetailsRequest> violation = violations.iterator().next();
        assertEquals("lastName", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should invalidate UserDetailsRequest with invalid email format")
    void shouldInvalidateUserDetailsRequestWithInvalidEmailFormat() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest request = new UserDetailsRequest(
                123L,
                "John",
                "Doe",
                "invalid-email",
                addressRequest
        );

        // Act
        Set<ConstraintViolation<UserDetailsRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<UserDetailsRequest> violation = violations.iterator().next();
        assertEquals("Neplatný formát emailu.", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should invalidate UserDetailsRequest with null address")
    void shouldInvalidateUserDetailsRequestWithNullAddress() {
        // Arrange
        UserDetailsRequest request = new UserDetailsRequest(
                123L,
                "John",
                "Doe",
                "john.doe@example.com",
                null
        );

        // Act
        Set<ConstraintViolation<UserDetailsRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<UserDetailsRequest> violation = violations.iterator().next();
        assertEquals("Adresa nesmí být prázdná.", violation.getMessage());
        assertEquals("address", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should create equal UserDetailsRequest with same values")
    void shouldCreateEqualUserDetailsRequestWithSameValues() {
        // Arrange
        AddressRequest addressRequest1 = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        AddressRequest addressRequest2 = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest request1 = new UserDetailsRequest(
                123L,
                "John",
                "Doe",
                "john.doe@example.com",
                addressRequest1
        );

        UserDetailsRequest request2 = new UserDetailsRequest(
                123L,
                "John",
                "Doe",
                "john.doe@example.com",
                addressRequest2
        );

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    @DisplayName("Should create different UserDetailsRequest with different values")
    void shouldCreateDifferentUserDetailsRequestWithDifferentValues() {
        // Arrange
        AddressRequest addressRequest1 = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        AddressRequest addressRequest2 = new AddressRequest(
                "456 Second Ave",
                "Los Angeles",
                "90001"
        );

        UserDetailsRequest request1 = new UserDetailsRequest(
                123L,
                "John",
                "Doe",
                "john.doe@example.com",
                addressRequest1
        );

        UserDetailsRequest request2 = new UserDetailsRequest(
                456L,
                "Jane",
                "Smith",
                "jane.smith@example.com",
                addressRequest2
        );

        // Act & Assert
        assertNotEquals(request1, request2);
        assertNotEquals(request1.hashCode(), request2.hashCode());
    }
}