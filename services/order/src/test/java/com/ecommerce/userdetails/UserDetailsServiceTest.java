package com.ecommerce.userdetails;

import com.ecommerce.address.AddressRequest;
import com.ecommerce.address.AddressService;
import com.ecommerce.order.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private UserDetailsMapper userDetailsMapper;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private UserDetailsService userDetailsService;

    @Captor
    private ArgumentCaptor<UserDetails> userDetailsCaptor;

    @Test
    @DisplayName("Should manage user details successfully")
    void shouldManageUserDetailsSuccessfully() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest userDetailsRequest = new UserDetailsRequest(
                123L,
                "John",
                "Doe",
                "john.doe@example.com",
                addressRequest
        );

        Order order = new Order();
        order.setId(1L);

        UserDetails userDetails = UserDetails.builder()
                .userId(userDetailsRequest.id())
                .firstName(userDetailsRequest.firstName())
                .lastName(userDetailsRequest.lastName())
                .email(userDetailsRequest.email())
                .build();

        UserDetails savedUserDetails = UserDetails.builder()
                .id(1L)
                .userId(userDetailsRequest.id())
                .firstName(userDetailsRequest.firstName())
                .lastName(userDetailsRequest.lastName())
                .email(userDetailsRequest.email())
                .order(order)
                .build();

        when(userDetailsMapper.toUserDetails(userDetailsRequest)).thenReturn(userDetails);
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(savedUserDetails);
        doNothing().when(addressService).manageAddress(addressRequest, savedUserDetails);

        // Act
        userDetailsService.manageUserDetails(userDetailsRequest, order);

        // Assert
        verify(userDetailsMapper).toUserDetails(userDetailsRequest);
        verify(userDetailsRepository).save(userDetailsCaptor.capture());
        verify(addressService).manageAddress(addressRequest, savedUserDetails);

        UserDetails capturedUserDetails = userDetailsCaptor.getValue();
        assertNotNull(capturedUserDetails);
        assertEquals(userDetailsRequest.id(), capturedUserDetails.getUserId());
        assertEquals(userDetailsRequest.firstName(), capturedUserDetails.getFirstName());
        assertEquals(userDetailsRequest.lastName(), capturedUserDetails.getLastName());
        assertEquals(userDetailsRequest.email(), capturedUserDetails.getEmail());
        assertEquals(order, capturedUserDetails.getOrder());
    }

    @Test
    @DisplayName("Should throw NullPointerException when UserDetailsRequest is null")
    void shouldThrowExceptionWhenUserDetailsRequestIsNull() {
        // Arrange
        Order order = new Order();
        order.setId(1L);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> userDetailsService.manageUserDetails(null, order));

        // Verify repository and address service are not called
        verify(userDetailsRepository, never()).save(any());
        verify(addressService, never()).manageAddress(any(), any());
    }

    @Test
    @DisplayName("Should throw NullPointerException when Order is null")
    void shouldThrowExceptionWhenOrderIsNull() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest userDetailsRequest = new UserDetailsRequest(
                123L,
                "John",
                "Doe",
                "john.doe@example.com",
                addressRequest
        );

        UserDetails userDetails = UserDetails.builder()
                .userId(userDetailsRequest.id())
                .firstName(userDetailsRequest.firstName())
                .lastName(userDetailsRequest.lastName())
                .email(userDetailsRequest.email())
                .build();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> userDetailsService.manageUserDetails(userDetailsRequest, null));

        // Verify repository and address service are not called, but mapper is called
        verify(userDetailsRepository, never()).save(any());
        verify(addressService, never()).manageAddress(any(), any());
    }

    @Test
    @DisplayName("Should save user details with order reference")
    void shouldSaveUserDetailsWithOrderReference() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest userDetailsRequest = new UserDetailsRequest(
                123L,
                "John",
                "Doe",
                "john.doe@example.com",
                addressRequest
        );

        Order order = new Order();
        order.setId(1L);

        UserDetails userDetails = UserDetails.builder()
                .userId(userDetailsRequest.id())
                .firstName(userDetailsRequest.firstName())
                .lastName(userDetailsRequest.lastName())
                .email(userDetailsRequest.email())
                .build();

        when(userDetailsMapper.toUserDetails(userDetailsRequest)).thenReturn(userDetails);
        when(userDetailsRepository.save(any(UserDetails.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        userDetailsService.manageUserDetails(userDetailsRequest, order);

        // Assert
        verify(userDetailsRepository).save(userDetailsCaptor.capture());

        UserDetails capturedUserDetails = userDetailsCaptor.getValue();
        assertEquals(order, capturedUserDetails.getOrder());
    }

    @Test
    @DisplayName("Should call address service with correct parameters")
    void shouldCallAddressServiceWithCorrectParameters() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                "123 Main St",
                "New York",
                "10001"
        );

        UserDetailsRequest userDetailsRequest = new UserDetailsRequest(
                123L,
                "John",
                "Doe",
                "john.doe@example.com",
                addressRequest
        );

        Order order = new Order();
        order.setId(1L);

        UserDetails userDetails = UserDetails.builder()
                .userId(userDetailsRequest.id())
                .firstName(userDetailsRequest.firstName())
                .lastName(userDetailsRequest.lastName())
                .email(userDetailsRequest.email())
                .build();

        UserDetails savedUserDetails = UserDetails.builder()
                .id(1L)
                .userId(userDetailsRequest.id())
                .firstName(userDetailsRequest.firstName())
                .lastName(userDetailsRequest.lastName())
                .email(userDetailsRequest.email())
                .order(order)
                .build();

        when(userDetailsMapper.toUserDetails(userDetailsRequest)).thenReturn(userDetails);
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(savedUserDetails);

        // Act
        userDetailsService.manageUserDetails(userDetailsRequest, order);

        // Assert
        verify(addressService).manageAddress(eq(addressRequest), eq(savedUserDetails));
    }
}