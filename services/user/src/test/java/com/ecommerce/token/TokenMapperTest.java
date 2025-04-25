package com.ecommerce.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TokenMapperTest {

    private TokenMapper tokenMapper;
    private static final long JWT_EXPIRATION = 900000; // 15 minutes
    private static final long REFRESH_EXPIRATION = 604800000; // 7 days

    @BeforeEach
    void setUp() {
        tokenMapper = new TokenMapper();
        ReflectionTestUtils.setField(tokenMapper, "jwtExpiration", JWT_EXPIRATION);
        ReflectionTestUtils.setField(tokenMapper, "refreshExpiration", REFRESH_EXPIRATION);
    }

    @Test
    void toAccessCookie_WithValidValue_ShouldReturnCookieWithCorrectAttributes() {
        // Arrange
        String tokenValue = "valid-access-token";

        // Act
        ResponseCookie cookie = tokenMapper.toAccessCookie(tokenValue);

        // Assert
        assertNotNull(cookie);
        assertEquals("access_token", cookie.getName());
        assertEquals(tokenValue, cookie.getValue());
        assertEquals(Duration.ofMillis(JWT_EXPIRATION), cookie.getMaxAge());
        assertEquals("/", cookie.getPath());
        assertEquals("Lax", cookie.getSameSite());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.isSecure());
    }

    @Test
    void toRefreshCookie_WithValidValue_ShouldReturnCookieWithCorrectAttributes() {
        // Arrange
        String tokenValue = "valid-refresh-token";

        // Act
        ResponseCookie cookie = tokenMapper.toRefreshCookie(tokenValue);

        // Assert
        assertNotNull(cookie);
        assertEquals("refresh_token", cookie.getName());
        assertEquals(tokenValue, cookie.getValue());
        assertEquals(Duration.ofMillis(REFRESH_EXPIRATION), cookie.getMaxAge());
        assertEquals("/", cookie.getPath());
        assertEquals("Lax", cookie.getSameSite());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.isSecure());
    }

    @Test
    void toAccessCookie_WithNullValue_ShouldThrowNullPointerException() {
        // Act & Assert
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> tokenMapper.toAccessCookie(null)
        );

        assertEquals("Hodnota nesmí být prázdná.", exception.getMessage());
    }

    @Test
    void toAccessCookie_WithBlankValue_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tokenMapper.toAccessCookie("")
        );

        assertEquals("Přístupový token nesmí být prázdný.", exception.getMessage());
    }

    @Test
    void toRefreshCookie_WithNullValue_ShouldThrowNullPointerException() {
        // Act & Assert
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> tokenMapper.toRefreshCookie(null)
        );

        assertEquals("Hodnota nesmí být prázdná.", exception.getMessage());
    }

    @Test
    void toRefreshCookie_WithBlankValue_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tokenMapper.toRefreshCookie("")
        );

        assertEquals("Obnovovací token nesmí být prázdný.", exception.getMessage());
    }

    @Test
    void discardAccessCookie_ShouldReturnCookieWithZeroMaxAge() {
        // Act
        ResponseCookie cookie = tokenMapper.discardAccessCookie();

        // Assert
        assertNotNull(cookie);
        assertEquals("access_token", cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals(Duration.ofMillis(0), cookie.getMaxAge());
        assertEquals("/", cookie.getPath());
        assertEquals("Lax", cookie.getSameSite());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.isSecure());
    }

    @Test
    void discardRefreshCookie_ShouldReturnCookieWithZeroMaxAge() {
        // Act
        ResponseCookie cookie = tokenMapper.discardRefreshCookie();

        // Assert
        assertNotNull(cookie);
        assertEquals("refresh_token", cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals(Duration.ofMillis(0), cookie.getMaxAge());
        assertEquals("/", cookie.getPath());
        assertEquals("Lax", cookie.getSameSite());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.isSecure());
    }
}