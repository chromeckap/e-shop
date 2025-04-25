package com.ecommerce.address;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AddressRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenAllFieldsValid_thenNoConstraintViolations() {
        // Arrange
        AddressRequest request = new AddressRequest("Jiráskova 25", "Plzeň", "301 00");

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void whenStreetIsNull_thenConstraintViolation() {
        // Arrange
        AddressRequest request = new AddressRequest(null, "Plzeň", "301 00");

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Ulice nesmí být prázdná");
    }

    @Test
    void whenStreetIsEmpty_thenConstraintViolation() {
        // Arrange
        AddressRequest request = new AddressRequest("", "Plzeň", "301 00");

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Ulice nesmí být prázdná");
    }

    @Test
    void whenStreetExceedsMaxLength_thenConstraintViolation() {
        // Arrange
        String tooLongStreet = "a".repeat(256);
        AddressRequest request = new AddressRequest(tooLongStreet, "Plzeň", "301 00");

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Ulice může mít maximálně 255 znaků");
    }

    @Test
    void whenCityIsNull_thenConstraintViolation() {
        // Arrange
        AddressRequest request = new AddressRequest("Jiráskova 25", null, "301 00");

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Město nesmí být prázdné");
    }

    @Test
    void whenCityIsEmpty_thenConstraintViolation() {
        // Arrange
        AddressRequest request = new AddressRequest("Jiráskova 25", "", "301 00");

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Město nesmí být prázdné");
    }

    @Test
    void whenCityExceedsMaxLength_thenConstraintViolation() {
        // Arrange
        String tooLongCity = "a".repeat(101);
        AddressRequest request = new AddressRequest("Jiráskova 25", tooLongCity, "301 00");

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Město může mít maximálně 100 znaků");
    }

    @Test
    void whenPostalCodeIsNull_thenConstraintViolation() {
        // Arrange
        AddressRequest request = new AddressRequest("Jiráskova 25", "Plzeň", null);

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("PSČ nesmí být prázdné");
    }

    @Test
    void whenPostalCodeIsEmpty_thenConstraintViolation() {
        // Arrange
        AddressRequest request = new AddressRequest("Jiráskova 25", "Plzeň", "");

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(2);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("PSČ musí být ve formátu 12345 nebo 123 45");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "123 45"})
    void whenPostalCodeValid_thenNoConstraintViolations(String postalCode) {
        // Arrange
        AddressRequest request = new AddressRequest("Jiráskova 25", "Plzeň", postalCode);

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "12 345", "123456", "12A45", "ABC 45", "abc12"})
    void whenPostalCodeInvalid_thenConstraintViolation(String postalCode) {
        // Arrange
        AddressRequest request = new AddressRequest("Jiráskova 25", "Plzeň", postalCode);

        // Act
        Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("PSČ musí být ve formátu 12345 nebo 123 45");
    }
}