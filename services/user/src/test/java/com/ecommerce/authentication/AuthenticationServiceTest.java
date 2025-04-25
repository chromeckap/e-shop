package com.ecommerce.authentication;

import com.ecommerce.authorization.Role;
import com.ecommerce.config.JwtService;
import com.ecommerce.exception.InvalidTokenException;
import com.ecommerce.exception.UnauthorizedAccessException;
import com.ecommerce.token.TokenMapper;
import com.ecommerce.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenMapper tokenMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private UserResponse userResponse;
    private ResponseCookie accessCookie;
    private ResponseCookie refreshCookie;

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

        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role("USER")
                .build();

        accessCookie = ResponseCookie.from("access_token", "access-token-value").build();
        refreshCookie = ResponseCookie.from("refresh_token", "refresh-token-value").build();
    }

    @Test
    void register_ShouldSaveUser() {
        // Arrange
        when(userMapper.toUser(registerRequest)).thenReturn(user);
        when(userRepository.findAll()).thenReturn(List.of(User.builder().build())); // Not empty

        // Act
        authenticationService.register(registerRequest);

        // Assert
        verify(userValidator).validateUserDoesNotExist(registerRequest.email());
        verify(userValidator).validatePasswordsEqual(registerRequest.password(), registerRequest.confirmPassword());
        verify(userMapper).toUser(registerRequest);
        verify(userRepository).save(user);
        assertEquals(Role.CUSTOMER, user.getRole()); // Role should remain CUSTOMER
    }

    @Test
    void register_WithEmptyUserRepository_ShouldSetRoleToAdmin() {
        // Arrange
        when(userMapper.toUser(registerRequest)).thenReturn(user);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        authenticationService.register(registerRequest);

        // Assert
        verify(userValidator).validateUserDoesNotExist(registerRequest.email());
        verify(userValidator).validatePasswordsEqual(registerRequest.password(), registerRequest.confirmPassword());
        verify(userMapper).toUser(registerRequest);
        verify(userRepository).save(user);
        assertEquals(Role.ADMIN, user.getRole()); // Role should be set to ADMIN
    }

    @Test
    void login_ShouldReturnUserResponse() {
        // Arrange
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        when(authenticationManager.authenticate(argThat(token ->
                token.getPrincipal().equals(loginRequest.email()) &&
                        token.getCredentials().equals(loginRequest.password()))))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        when(tokenMapper.toAccessCookie("access-token")).thenReturn(accessCookie);
        when(tokenMapper.toRefreshCookie("refresh-token")).thenReturn(refreshCookie);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        UserResponse result = authenticationService.login(loginRequest, response);

        // Assert
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
        verify(tokenMapper).toAccessCookie("access-token");
        verify(tokenMapper).toRefreshCookie("refresh-token");
        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
        verify(userMapper).toResponse(user);
        assertEquals(userResponse, result);
    }

    @Test
    void refreshToken_ShouldReturnUserResponse() {
        // Arrange
        String refreshToken = "refresh-token";
        when(jwtService.extractTokenFromCookies(request, "refresh_token")).thenReturn(refreshToken);
        when(jwtService.extractEmail(refreshToken)).thenReturn("john.doe@example.com");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");
        when(tokenMapper.toAccessCookie("new-access-token")).thenReturn(accessCookie);
        when(tokenMapper.toRefreshCookie("new-refresh-token")).thenReturn(refreshCookie);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        UserResponse result = authenticationService.refreshToken(request, response);

        // Assert
        verify(jwtService).extractTokenFromCookies(request, "refresh_token");
        verify(jwtService).extractEmail(refreshToken);
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(jwtService).isTokenValid(refreshToken, user);
        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
        verify(tokenMapper).toAccessCookie("new-access-token");
        verify(tokenMapper).toRefreshCookie("new-refresh-token");
        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
        verify(userMapper).toResponse(user);
        assertEquals(userResponse, result);
    }

    @Test
    void refreshToken_WithMissingToken_ShouldThrowInvalidTokenException() {
        // Arrange
        when(jwtService.extractTokenFromCookies(request, "refresh_token")).thenReturn(null);

        // Act & Assert
        InvalidTokenException exception = assertThrows(
                InvalidTokenException.class,
                () -> authenticationService.refreshToken(request, response)
        );

        assertEquals("Obnovovací token nesmí chybět.", exception.getMessage());
        verify(tokenMapper, never()).toAccessCookie(anyString());
        verify(tokenMapper, never()).toRefreshCookie(anyString());
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldThrowInvalidTokenException() {
        // Arrange
        String refreshToken = "refresh-token";
        when(jwtService.extractTokenFromCookies(request, "refresh_token")).thenReturn(refreshToken);
        when(jwtService.extractEmail(refreshToken)).thenReturn("john.doe@example.com");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(false);

        // Act & Assert
        InvalidTokenException exception = assertThrows(
                InvalidTokenException.class,
                () -> authenticationService.refreshToken(request, response)
        );

        assertEquals("Neplatný obnovovací token.", exception.getMessage());
        verify(tokenMapper, never()).toAccessCookie(anyString());
        verify(tokenMapper, never()).toRefreshCookie(anyString());
    }

    @Test
    void refreshUserInfo_ShouldReturnUserResponse() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john.doe@example.com");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        UserResponse result = authenticationService.refreshUserInfo();

        // Assert
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(userMapper).toResponse(user);
        assertEquals(userResponse, result);
    }

    @Test
    void refreshUserInfo_WithAnonymousUser_ShouldThrowUnauthorizedAccessException() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> authenticationService.refreshUserInfo()
        );

        assertEquals("Nemáš dostatečné oprávnění.", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void validateRequest_ShouldReturnGatewayUserResponse() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authentication.getName()).thenReturn("john.doe@example.com");
        Collection<? extends GrantedAuthority> adminAuthorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        doReturn(adminAuthorities).when(authentication).getAuthorities();

        // Act
        GatewayUserResponse result = authenticationService.validateRequest();

        // Assert
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(authentication).getPrincipal();
        verify(authentication).getName();
        verify(authentication).getAuthorities();
        assertEquals(1L, result.id());
        assertEquals("john.doe@example.com", result.email());
        assertEquals(Set.of("ROLE_CUSTOMER"), result.roles());
    }

    @Test
    void validateRequest_WithNonUserPrincipal_ShouldReturnGatewayUserResponseWithNullId() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("principal-string");
        when(authentication.getName()).thenReturn("john.doe@example.com");
        Collection<? extends GrantedAuthority> adminAuthorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        doReturn(adminAuthorities).when(authentication).getAuthorities();

        // Act
        GatewayUserResponse result = authenticationService.validateRequest();

        // Assert
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(authentication).getPrincipal();
        verify(authentication).getName();
        verify(authentication).getAuthorities();
        assertNull(result.id());
        assertEquals("john.doe@example.com", result.email());
        assertEquals(Set.of("ROLE_CUSTOMER"), result.roles());
    }

    @Test
    void validateRequest_WithNoAuthentication_ShouldThrowIllegalArgumentException() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authenticationService.validateRequest()
        );

        assertEquals("No authenticated user found.", exception.getMessage());
    }
}