package com.ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_WithValidHeaders_SetsAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-User-Username")).thenReturn("testuser");
        when(request.getHeader("X-User-Roles")).thenReturn("[ROLE_USER, ROLE_ADMIN]");

        // Act
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // Capture the authentication object
        ArgumentCaptor<Authentication> authCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(securityContext).setAuthentication(authCaptor.capture());
        Authentication authentication = authCaptor.getValue();

        // Assert
        assertNotNull(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assertEquals("testuser", userDetails.getUsername());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // Verify that filter chain continues
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithoutHeaders_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-User-Username")).thenReturn(null);
        when(request.getHeader("X-User-Roles")).thenReturn(null);

        // Act
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithOnlyUsernameHeader_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-User-Username")).thenReturn("testuser");
        when(request.getHeader("X-User-Roles")).thenReturn(null);

        // Act
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithOnlyRolesHeader_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-User-Username")).thenReturn(null);
        when(request.getHeader("X-User-Roles")).thenReturn("[ROLE_USER]");

        // Act
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithEmptyRoles_HandlesProperly() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-User-Username")).thenReturn("testuser");
        when(request.getHeader("X-User-Roles")).thenReturn("[]");

        // Act
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        ArgumentCaptor<Authentication> authCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(securityContext).setAuthentication(authCaptor.capture());
        Authentication authentication = authCaptor.getValue();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().isEmpty(), "Authorities should be empty for empty roles");

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithSpecialCharactersInRoles_ParsesCorrectly() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-User-Username")).thenReturn("testuser");
        when(request.getHeader("X-User-Roles")).thenReturn("[ROLE_SUPER-USER, ROLE_ADMIN_2]");

        // Act
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        ArgumentCaptor<Authentication> authCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(securityContext).setAuthentication(authCaptor.capture());
        Authentication authentication = authCaptor.getValue();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_SUPER-USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN_2")));
    }

    @Test
    void doFilterInternal_WhenExceptionOccurs_ContinuesFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-User-Username")).thenReturn("testuser");
        when(request.getHeader("X-User-Roles")).thenReturn("[ROLE_USER]");

        // Act & Assert
        assertDoesNotThrow(() ->
                authenticationFilter.doFilterInternal(request, response, filterChain));

        // Verify filter chain still continues despite the exception
        verify(filterChain, times(1)).doFilter(request, response);
    }
}