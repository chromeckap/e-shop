package com.ecommerce.authentication;

import com.ecommerce.user.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationController authenticationController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserResponse userResponse;
    private GatewayUserResponse gatewayUserResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "password123"
        );

        loginRequest = new LoginRequest(
                "john.doe@example.com",
                "password123"
        );

        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role("USER")
                .build();

        gatewayUserResponse = GatewayUserResponse.builder()
                .id(1L)
                .email("john.doe@example.com")
                .roles(java.util.Set.of("USER"))
                .build();
    }

    @Test
    void register_ShouldReturnCreatedStatus() {
        // Arrange
        doNothing().when(authenticationService).register(registerRequest);

        // Act
        ResponseEntity<Void> responseEntity = authenticationController.register(registerRequest);

        // Assert
        verify(authenticationService, times(1)).register(registerRequest);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void login_ShouldReturnUserResponse() {
        // Arrange
        when(authenticationService.login(loginRequest, response)).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> responseEntity = authenticationController.login(loginRequest, response);

        // Assert
        verify(authenticationService, times(1)).login(loginRequest, response);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());
    }

    @Test
    void refreshToken_ShouldReturnUserResponse() {
        // Arrange
        when(authenticationService.refreshToken(request, response)).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> responseEntity = authenticationController.refreshToken(request, response);

        // Assert
        verify(authenticationService, times(1)).refreshToken(request, response);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());
    }

    @Test
    void refreshUserInfo_ShouldReturnUserResponse() {
        // Arrange
        when(authenticationService.refreshUserInfo()).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> responseEntity = authenticationController.refreshUserInfo();

        // Assert
        verify(authenticationService, times(1)).refreshUserInfo();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());
    }

    @Test
    void validateRequest_ShouldReturnGatewayUserResponse() {
        // Arrange
        when(authenticationService.validateRequest()).thenReturn(gatewayUserResponse);

        // Act
        ResponseEntity<GatewayUserResponse> responseEntity = authenticationController.validateRequest(request);

        // Assert
        verify(authenticationService, times(1)).validateRequest();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(gatewayUserResponse, responseEntity.getBody());
    }
}