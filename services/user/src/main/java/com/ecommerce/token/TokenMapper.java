package com.ecommerce.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
public class TokenMapper {
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String PATH = "/";

    public ResponseCookie toAccessCookie(String value) {
        Objects.requireNonNull(value, "Hodnota nesmí být prázdná.");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Přístupový token nesmí být prázdný.");
        }
        return this.buildCookie(ACCESS_TOKEN, value, jwtExpiration);
    }

    public ResponseCookie toRefreshCookie(String value) {
        Objects.requireNonNull(value, "Hodnota nesmí být prázdná.");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Obnovovací token nesmí být prázdný.");
        }
        return buildCookie(REFRESH_TOKEN, value, refreshExpiration);
    }

    public ResponseCookie discardAccessCookie() {
        return this.buildCookie(ACCESS_TOKEN, "", 0);
    }

    public ResponseCookie discardRefreshCookie() {
        return this.buildCookie(REFRESH_TOKEN, "", 0);
    }

    private ResponseCookie buildCookie(String name, String value, long maxAgeMillis) {
        return ResponseCookie.from(name, value)
                .sameSite("Lax")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofMillis(maxAgeMillis))
                .path(PATH)
                .build();
    }

}