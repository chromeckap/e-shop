package com.ecommerce.address;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AddressMapperTest {

    @InjectMocks
    private AddressMapper addressMapper;

    @Test
    void toAddress_ShouldMapAddressRequestToAddress() {
        // Arrange
        AddressRequest request = new AddressRequest("Hlavní třída 123", "Brno", "602 00");

        // Act
        Address result = addressMapper.toAddress(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStreet()).isEqualTo("Hlavní třída 123");
        assertThat(result.getCity()).isEqualTo("Brno");
        assertThat(result.getPostalCode()).isEqualTo("602 00");
        assertThat(result.getId()).isNull();
        assertThat(result.getUserDetails()).isNull();
    }

    @Test
    void toAddress_ShouldThrowNullPointerException_WhenRequestIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> addressMapper.toAddress(null));
    }

    @Test
    void toResponse_ShouldMapAddressToAddressResponse() {
        // Arrange
        Address address = Address.builder()
                .id(1L)
                .street("Palackého třída 45")
                .city("Olomouc")
                .postalCode("779 00")
                .build();

        // Act
        AddressResponse result = addressMapper.toResponse(address);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.street()).isEqualTo("Palackého třída 45");
        assertThat(result.city()).isEqualTo("Olomouc");
        assertThat(result.postalCode()).isEqualTo("779 00");
    }

    @Test
    void toResponse_ShouldThrowNullPointerException_WhenAddressIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> addressMapper.toResponse(null));
    }
}