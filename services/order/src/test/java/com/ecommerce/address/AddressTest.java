package com.ecommerce.address;

import com.ecommerce.userdetails.UserDetails;
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

class AddressTest {

    private Validator validator;
    private Address address;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        userDetails = new UserDetails();
        userDetails.setId(1L);

        address = Address.builder()
                .id(1L)
                .street("Masarykova 10")
                .city("Praha")
                .postalCode("110 00")
                .userDetails(userDetails)
                .build();
    }

    @Test
    void whenAllFieldsValid_thenNoConstraintViolations() {
        // Act
        Set<ConstraintViolation<Address>> violations = validator.validate(address);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void whenStreetIsNull_thenConstraintViolation() {
        // Arrange
        address.setStreet(null);

        // Act
        Set<ConstraintViolation<Address>> violations = validator.validate(address);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("nesmí být prázdná");
    }

    @Test
    void whenStreetIsEmpty_thenConstraintViolation() {
        // Arrange
        address.setStreet("");

        // Act
        Set<ConstraintViolation<Address>> violations = validator.validate(address);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("nesmí být prázdná");
    }

    @Test
    void whenStreetExceedsMaxLength_thenConstraintViolation() {
        // Arrange
        String tooLongStreet = "a".repeat(256);
        address.setStreet(tooLongStreet);

        // Act
        Set<ConstraintViolation<Address>> violations = validator.validate(address);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("velikost musí ležet v rozsahu 0 až 255");
    }

    @Test
    void whenCityIsNull_thenConstraintViolation() {
        // Arrange
        address.setCity(null);

        // Act
        Set<ConstraintViolation<Address>> violations = validator.validate(address);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("nesmí být prázdná");
    }

    @Test
    void whenCityIsEmpty_thenConstraintViolation() {
        // Arrange
        address.setCity("");

        // Act
        Set<ConstraintViolation<Address>> violations = validator.validate(address);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("nesmí být prázdná");
    }

    @Test
    void whenCityExceedsMaxLength_thenConstraintViolation() {
        // Arrange
        String tooLongCity = "a".repeat(101);
        address.setCity(tooLongCity);

        // Act
        Set<ConstraintViolation<Address>> violations = validator.validate(address);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("velikost musí ležet v rozsahu 0 až 100");
    }

    @Test
    void whenPostalCodeIsNull_thenConstraintViolation() {
        // Arrange
        address.setPostalCode(null);

        // Act
        Set<ConstraintViolation<Address>> violations = validator.validate(address);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("nesmí být prázdná");
    }

    @Test
    void whenPostalCodeIsEmpty_thenConstraintViolation() {
        // Arrange
        address.setPostalCode("");

        // Act
        Set<ConstraintViolation<Address>> violations = validator.validate(address);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("nesmí být prázdná");
    }

    @Test
    void whenPostalCodeExceedsMaxLength_thenConstraintViolation() {
        // Arrange
        String tooLongPostalCode = "a".repeat(21);
        address.setPostalCode(tooLongPostalCode);

        // Act
        Set<ConstraintViolation<Address>> violations = validator.validate(address);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("velikost musí ležet v rozsahu 0 až 20");
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Address address1 = Address.builder().id(1L).build();
        Address address2 = Address.builder().id(1L).build();
        Address address3 = Address.builder().id(2L).build();

        // Assert
        assertThat(address1).isEqualTo(address2);
        assertThat(address1).isNotEqualTo(address3);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
        assertThat(address1.hashCode()).isNotEqualTo(address3.hashCode());
    }

    @Test
    void testBuilder() {
        // Act
        Address builtAddress = Address.builder()
                .id(2L)
                .street("Národní 5")
                .city("Brno")
                .postalCode("602 00")
                .userDetails(userDetails)
                .build();

        // Assert
        assertThat(builtAddress.getId()).isEqualTo(2L);
        assertThat(builtAddress.getStreet()).isEqualTo("Národní 5");
        assertThat(builtAddress.getCity()).isEqualTo("Brno");
        assertThat(builtAddress.getPostalCode()).isEqualTo("602 00");
        assertThat(builtAddress.getUserDetails()).isEqualTo(userDetails);
    }
}