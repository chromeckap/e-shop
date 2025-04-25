package com.ecommerce.config;

import com.ecommerce.token.TokenMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenMapper tokenMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Captor
    private ArgumentCaptor<Authentication> authenticationCaptor;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        // Create a test user
        userDetails = new User(
                "test@example.com",
                "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Set up a fresh security context for each test
        securityContext = new SecurityContextImpl();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_ShouldSkipFiltering_ForRegisterEndpoint() throws IOException, ServletException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/v1/auth/register");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractTokenFromCookies(any(), anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ShouldSkipFiltering_ForLoginEndpoint() throws IOException, ServletException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/v1/auth/login");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractTokenFromCookies(any(), anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ShouldSkipFiltering_WhenNoAccessToken() throws IOException, ServletException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/v1/other/endpoint");
        when(jwtService.extractTokenFromCookies(request, "access_token")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractTokenFromCookies(request, "access_token");
        verify(jwtService, never()).extractEmail(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ShouldSetAuthentication_ForValidToken() throws IOException, ServletException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/v1/other/endpoint");
        when(jwtService.extractTokenFromCookies(request, "access_token")).thenReturn("valid-token");
        when(jwtService.extractEmail("valid-token")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractTokenFromCookies(request, "access_token");
        verify(jwtService).extractEmail("valid-token");
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(jwtService).isTokenValid("valid-token", userDetails);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertEquals(userDetails, authentication.getPrincipal());
    }

    @Test
    void doFilterInternal_ShouldNotSetAuthentication_ForInvalidToken() throws IOException, ServletException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/v1/other/endpoint");
        when(jwtService.extractTokenFromCookies(request, "access_token")).thenReturn("invalid-token");
        when(jwtService.extractEmail("invalid-token")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("invalid-token", userDetails)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractTokenFromCookies(request, "access_token");
        verify(jwtService).extractEmail("invalid-token");
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(jwtService).isTokenValid("invalid-token", userDetails);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ShouldHandleException_AndDiscardCookies() throws IOException, ServletException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/v1/other/endpoint");
        when(jwtService.extractTokenFromCookies(request, "access_token")).thenReturn("token");
        when(jwtService.extractEmail("token")).thenThrow(new RuntimeException("Token parsing error"));

        ResponseCookie accessCookie = ResponseCookie.from("access_token", "").build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "").build();

        when(tokenMapper.discardAccessCookie()).thenReturn(accessCookie);
        when(tokenMapper.discardRefreshCookie()).thenReturn(refreshCookie);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtService).extractTokenFromCookies(request, "access_token");
        verify(jwtService).extractEmail("token");
        verify(tokenMapper).discardAccessCookie();
        verify(tokenMapper).discardRefreshCookie();
        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalidní token.");
        verify(filterChain, never()).doFilter(request, response);
    }
}