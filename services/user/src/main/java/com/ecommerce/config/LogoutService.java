package com.ecommerce.config;

import com.ecommerce.token.TokenMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenMapper tokenMapper;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        ResponseCookie accessTokenCookie = tokenMapper.discardAccessCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        ResponseCookie refreshTokenCookie = tokenMapper.discardRefreshCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }
}