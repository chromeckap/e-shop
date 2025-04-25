package com.ecommerce.user;

import com.ecommerce.settings.Constants;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse userResponse;
    private Page<UserResponse> userResponsePage;

    @BeforeEach
    void setUp() {
        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role("CUSTOMER")
                .createTime(LocalDateTime.now())
                .build();

        userResponsePage = new PageImpl<>(List.of(userResponse));
    }

    @Test
    void getUserById_ShouldReturnUserResponse() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService).getUserById(1L);
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUserResponses_WithDefaultParameters() {
        // Arrange
        PageRequest expectedPageRequest = PageRequest.of(
                Constants.PAGE_NUMBER,
                Constants.PAGE_SIZE,
                Sort.by(Sort.Direction.fromString(Constants.DIRECTION), Constants.DEFAULT_SORT_ATTRIBUTE)
        );

        when(userService.getAllUsers(any(PageRequest.class))).thenReturn(userResponsePage);

        // Act
        ResponseEntity<Page<UserResponse>> response = userController.getAllUsers(
                Constants.PAGE_NUMBER,
                Constants.PAGE_SIZE,
                Constants.DIRECTION,
                Constants.DEFAULT_SORT_ATTRIBUTE
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponsePage, response.getBody());
        verify(userService).getAllUsers(refEq(expectedPageRequest, "sort"));
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUserResponses_WithCustomParameters() {
        // Arrange
        int pageNumber = 2;
        int pageSize = 20;
        String direction = "DESC";
        String attribute = "email";

        PageRequest expectedPageRequest = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.fromString(direction), attribute)
        );

        when(userService.getAllUsers(any(PageRequest.class))).thenReturn(userResponsePage);

        // Act
        ResponseEntity<Page<UserResponse>> response = userController.getAllUsers(
                pageNumber,
                pageSize,
                direction,
                attribute
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponsePage, response.getBody());
        verify(userService).getAllUsers(refEq(expectedPageRequest, "sort"));
    }

    @Test
    void updateUserRole_ShouldReturnAcceptedStatus() {
        // Arrange
        doNothing().when(userService).updateUserRole(1L, "ADMIN");

        // Act
        ResponseEntity<Void> response = userController.updateUserRole(1L, "ADMIN");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).updateUserRole(1L, "ADMIN");
    }

    @Test
    void deleteUserById_ShouldReturnNoContentStatus() {
        // Arrange
        doNothing().when(userService).deleteUserById(1L);

        // Act
        ResponseEntity<Void> response = userController.deleteUserById(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).deleteUserById(1L);
    }
}