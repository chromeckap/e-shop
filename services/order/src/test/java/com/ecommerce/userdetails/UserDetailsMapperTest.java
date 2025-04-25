package com.ecommerce.userdetails;

import com.ecommerce.address.Address;
import com.ecommerce.address.AddressMapper;
import com.ecommerce.address.AddressRequest;
import com.ecommerce.address.AddressResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsMapperTest {

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private UserDetailsMapper userDetailsMapper;

    @Test
    @DisplayName("Should map UserDetailsRequest to UserDetails")
    void shouldMapUserDetailsRequestToUserDetails() {
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
        UserDetails result = userDetailsMapper.toUserDetails(request);

        // Assert
        assertNotNull(result);
        assertEquals(request.id(), result.getUserId());
        assertEquals(request.firstName(), result.getFirstName());
        assertEquals(request.lastName(), result.getLastName());
        assertEquals(request.email(), result.getEmail());
        assertNull(result.getAddress());
        assertNull(result.getOrder());
    }

    @Test
    @DisplayName("Should throw NullPointerException when UserDetailsRequest is null")
    void shouldThrowExceptionWhenUserDetailsRequestIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> userDetailsMapper.toUserDetails(null));
    }

    @Test
    @DisplayName("Should map UserDetails to UserDetailsResponse")
    void shouldMapUserDetailsToUserDetailsResponse() {
        // Arrange
        Address address = new Address();
        address.setId(1L);
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setPostalCode("10001");

        UserDetails userDetails = UserDetails.builder()
                .id(1L)
                .userId(123L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .address(address)
                .build();

        AddressResponse addressResponse = AddressResponse.builder()
                .id(1L)
                .street("123 Main St")
                .city("New York")
                .postalCode("10001")
                .build();

        when(addressMapper.toResponse(address)).thenReturn(addressResponse);

        // Act
        UserDetailsResponse result = userDetailsMapper.toResponse(userDetails);

        // Assert
        assertNotNull(result);
        assertEquals(userDetails.getId(), result.id());
        assertEquals(userDetails.getUserId(), result.userId());
        assertEquals(userDetails.getFirstName(), result.firstName());
        assertEquals(userDetails.getLastName(), result.lastName());
        assertEquals(userDetails.getEmail(), result.email());
        assertEquals(addressResponse, result.address());

        verify(addressMapper).toResponse(address);
    }

    @Test
    @DisplayName("Should map UserDetails to UserDetailsResponse when Address is null")
    void shouldMapUserDetailsToUserDetailsResponseWhenAddressIsNull() {
        // Arrange
        UserDetails userDetails = UserDetails.builder()
                .id(1L)
                .userId(123L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .address(null)
                .build();

        when(addressMapper.toResponse(null)).thenReturn(null);

        // Act
        UserDetailsResponse result = userDetailsMapper.toResponse(userDetails);

        // Assert
        assertNotNull(result);
        assertEquals(userDetails.getId(), result.id());
        assertEquals(userDetails.getUserId(), result.userId());
        assertEquals(userDetails.getFirstName(), result.firstName());
        assertEquals(userDetails.getLastName(), result.lastName());
        assertEquals(userDetails.getEmail(), result.email());
        assertNull(result.address());

        verify(addressMapper).toResponse(null);
    }

    @Test
    @DisplayName("Should throw NullPointerException when UserDetails is null")
    void shouldThrowExceptionWhenUserDetailsIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> userDetailsMapper.toResponse(null));
    }
}