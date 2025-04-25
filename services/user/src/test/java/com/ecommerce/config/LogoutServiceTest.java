package com.ecommerce.config;

import com.ecommerce.token.TokenMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private TokenMapper tokenMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    void logout_ShouldDiscardTokenCookies() {
        // Arrange
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "").build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "").build();

        when(tokenMapper.discardAccessCookie()).thenReturn(accessCookie);
        when(tokenMapper.discardRefreshCookie()).thenReturn(refreshCookie);

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenMapper).discardAccessCookie();
        verify(tokenMapper).discardRefreshCookie();
        verify(response).addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        verify(response).addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}