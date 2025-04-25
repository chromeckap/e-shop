package com.ecommerce.address;

import com.ecommerce.userdetails.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private AddressRequest validRequest;
    private Address mappedAddress;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Setup test data
        validRequest = new AddressRequest("Václavské náměstí 1", "Praha", "110 00");

        mappedAddress = Address.builder()
                .street("Václavské náměstí 1")
                .city("Praha")
                .postalCode("110 00")
                .build();

        userDetails = new UserDetails();
        userDetails.setId(1L);
    }

    @Test
    void manageAddress_ShouldSaveAddressWithUserDetails() {
        // Arrange
        when(addressMapper.toAddress(validRequest)).thenReturn(mappedAddress);
        when(addressRepository.save(any(Address.class))).thenReturn(mappedAddress);

        // Act
        addressService.manageAddress(validRequest, userDetails);

        // Assert
        verify(addressMapper, times(1)).toAddress(validRequest);
        verify(addressRepository, times(1)).save(mappedAddress);
        assertThat(mappedAddress.getUserDetails()).isEqualTo(userDetails);
    }

    @Test
    void manageAddress_ShouldSetUserDetailsOnAddress() {
        // Arrange
        when(addressMapper.toAddress(validRequest)).thenReturn(mappedAddress);

        // Act
        addressService.manageAddress(validRequest, userDetails);

        // Assert
        assertThat(mappedAddress.getUserDetails()).isSameAs(userDetails);
    }
}