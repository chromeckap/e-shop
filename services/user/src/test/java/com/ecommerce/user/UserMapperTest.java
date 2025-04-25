package com.ecommerce.user;

import com.ecommerce.authentication.RegisterRequest;
import com.ecommerce.authorization.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserMapper userMapper;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "password123"
        );

        LocalDateTime createTime = LocalDateTime.now();
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encoded_password")
                .role(Role.CUSTOMER)
                .createTime(createTime)
                .build();
    }

    @Test
    void toUser_ShouldConvertRegisterRequestToUser() {
        // Arrange
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        // Act
        User result = userMapper.toUser(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(registerRequest.firstName(), result.getFirstName());
        assertEquals(registerRequest.lastName(), result.getLastName());
        assertEquals(registerRequest.email(), result.getEmail());
        assertEquals("encoded_password", result.getPassword());
        assertEquals(Role.CUSTOMER, result.getRole());

        verify(passwordEncoder).encode("password123");
    }

    @Test
    void toUser_ShouldThrowException_WhenRegisterRequestIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> userMapper.toUser(null)
        );
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void toResponse_ShouldConvertUserToUserResponse() {
        // Act
        UserResponse result = userMapper.toResponse(user);

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getFirstName(), result.firstName());
        assertEquals(user.getLastName(), result.lastName());
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getRole().name(), result.role());
        assertEquals(user.getCreateTime(), result.createTime());
    }

    @Test
    void toResponse_ShouldThrowException_WhenUserIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> userMapper.toResponse(null)
        );
    }
}