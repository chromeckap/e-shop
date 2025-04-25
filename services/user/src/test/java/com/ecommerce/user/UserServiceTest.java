package com.ecommerce.user;

import com.ecommerce.authorization.Role;
import com.ecommerce.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encoded_password")
                .role(Role.CUSTOMER)
                .createTime(LocalDateTime.now())
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role("CUSTOMER")
                .createTime(user.getCreateTime())
                .build();

        pageRequest = PageRequest.of(0, 10, Sort.by("id"));
    }

    @Test
    void findUserEntityById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findUserEntityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void findUserEntityById_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserEntityById(99L)
        );
        assertEquals("Uživatel s ID 99 nebyl nalezen.", exception.getMessage());
        verify(userRepository).findById(99L);
    }

    @Test
    void findUserEntityById_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> userService.findUserEntityById(null)
        );
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        UserResponse result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUserResponses() {
        // Arrange
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageRequest)).thenReturn(userPage);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        Page<UserResponse> result = userService.getAllUsers(pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userResponse, result.getContent().getFirst());
        verify(userRepository).findAll(pageRequest);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getAllUsers_ShouldThrowException_WhenRequestIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> userService.getAllUsers(null)
        );
        verify(userRepository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void updateUserRole_ShouldUpdateRole_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.updateUserRole(1L, "ADMIN");

        // Assert
        assertEquals(Role.ADMIN, user.getRole());
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserRole_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUserRole(99L, "ADMIN")
        );
        verify(userRepository).findById(99L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserRole_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> userService.updateUserRole(null, "ADMIN")
        );
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserRole_ShouldThrowException_WhenRoleIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> userService.updateUserRole(1L, null)
        );
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUserById_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUserById(1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserById_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUserById(99L)
        );
        verify(userRepository).findById(99L);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUserById_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> userService.deleteUserById(null)
        );
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).delete(any());
    }
}