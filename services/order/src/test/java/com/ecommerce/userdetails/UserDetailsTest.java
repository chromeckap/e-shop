package com.ecommerce.userdetails;

import com.ecommerce.address.Address;
import com.ecommerce.order.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsTest {

    @Test
    @DisplayName("Should create UserDetails with all fields")
    void shouldCreateUserDetailsWithAllFields() {
        // Arrange
        Long id = 1L;
        Long userId = 123L;
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        Address address = new Address();
        Order order = new Order();

        // Act
        UserDetails userDetails = UserDetails.builder()
                .id(id)
                .userId(userId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .address(address)
                .order(order)
                .build();

        // Assert
        assertNotNull(userDetails);
        assertEquals(id, userDetails.getId());
        assertEquals(userId, userDetails.getUserId());
        assertEquals(firstName, userDetails.getFirstName());
        assertEquals(lastName, userDetails.getLastName());
        assertEquals(email, userDetails.getEmail());
        assertEquals(address, userDetails.getAddress());
        assertEquals(order, userDetails.getOrder());
    }

    @Test
    @DisplayName("Should create equal UserDetails with same id")
    void shouldCreateEqualUserDetailsWithSameId() {
        // Arrange
        UserDetails userDetails1 = UserDetails.builder()
                .id(1L)
                .userId(123L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        UserDetails userDetails2 = UserDetails.builder()
                .id(1L)
                .userId(456L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();

        // Act & Assert
        assertEquals(userDetails1, userDetails2);
        assertEquals(userDetails1.hashCode(), userDetails2.hashCode());
    }

    @Test
    @DisplayName("Should create different UserDetails with different ids")
    void shouldCreateDifferentUserDetailsWithDifferentIds() {
        // Arrange
        UserDetails userDetails1 = UserDetails.builder()
                .id(1L)
                .userId(123L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        UserDetails userDetails2 = UserDetails.builder()
                .id(2L)
                .userId(123L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        // Act & Assert
        assertNotEquals(userDetails1, userDetails2);
        assertNotEquals(userDetails1.hashCode(), userDetails2.hashCode());
    }

    @Test
    @DisplayName("Should set and get fields correctly")
    void shouldSetAndGetFieldsCorrectly() {
        // Arrange
        UserDetails userDetails = new UserDetails();

        // Act
        userDetails.setId(1L);
        userDetails.setUserId(123L);
        userDetails.setFirstName("John");
        userDetails.setLastName("Doe");
        userDetails.setEmail("john.doe@example.com");

        Address address = new Address();
        userDetails.setAddress(address);

        Order order = new Order();
        userDetails.setOrder(order);

        // Assert
        assertEquals(1L, userDetails.getId());
        assertEquals(123L, userDetails.getUserId());
        assertEquals("John", userDetails.getFirstName());
        assertEquals("Doe", userDetails.getLastName());
        assertEquals("john.doe@example.com", userDetails.getEmail());
        assertEquals(address, userDetails.getAddress());
        assertEquals(order, userDetails.getOrder());
    }

    @Test
    @DisplayName("Should generate toString representation")
    void shouldGenerateToStringRepresentation() {
        // Arrange
        UserDetails userDetails = UserDetails.builder()
                .id(1L)
                .userId(123L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        // Act
        String toString = userDetails.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("userId=123"));
        assertTrue(toString.contains("firstName=John"));
        assertTrue(toString.contains("lastName=Doe"));
        assertTrue(toString.contains("email=john.doe@example.com"));
    }
}