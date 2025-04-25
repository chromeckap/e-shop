package com.ecommerce.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long jwtExpiration = 86400000; // 1 day
    private final long refreshExpiration = 604800000; // 7 days

    @BeforeEach
    void setUp() {
        // Initialize the JwtService with test values
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshExpiration);

        // Create a test user
        userDetails = new User(
                "test@example.com",
                "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void extractTokenFromCookies_ShouldReturnTokenValue_WhenTokenExists() {
        // Arrange
        Cookie[] cookies = new Cookie[]{
                new Cookie("access_token", "test-access-token"),
                new Cookie("refresh_token", "test-refresh-token")
        };
        when(request.getCookies()).thenReturn(cookies);

        // Act
        String accessToken = jwtService.extractTokenFromCookies(request, "access_token");
        String refreshToken = jwtService.extractTokenFromCookies(request, "refresh_token");

        // Assert
        assertEquals("test-access-token", accessToken);
        assertEquals("test-refresh-token", refreshToken);
    }

    @Test
    void extractTokenFromCookies_ShouldReturnNull_WhenTokenDoesNotExist() {
        // Arrange
        Cookie[] cookies = new Cookie[]{
                new Cookie("access_token", "test-access-token")
        };
        when(request.getCookies()).thenReturn(cookies);

        // Act
        String token = jwtService.extractTokenFromCookies(request, "non_existent_token");

        // Assert
        assertNull(token);
    }

    @Test
    void generateAccessToken_ShouldCreateValidToken() {
        // Act
        String token = jwtService.generateAccessToken(userDetails);

        // Assert
        assertNotNull(token);
        assertEquals("test@example.com", jwtService.extractEmail(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void generateRefreshToken_ShouldCreateValidToken() {
        // Act
        String token = jwtService.generateRefreshToken(userDetails);

        // Assert
        assertNotNull(token);
        assertEquals("test@example.com", jwtService.extractEmail(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void generateToken_WithExtraClaims_ShouldIncludeClaimsInToken() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Assert
        assertNotNull(token);
        assertEquals("customValue", jwtService.extractClaim(token, claims -> claims.get("customClaim")));
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        // Arrange
        String token = jwtService.generateAccessToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForDifferentUser() {
        // Arrange
        String token = jwtService.generateAccessToken(userDetails);

        UserDetails differentUser = new User(
                "different@example.com",
                "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractClaim_ShouldReturnClaimValue() {
        // Arrange
        String token = jwtService.generateAccessToken(userDetails);

        // Act
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

        // Assert
        assertEquals("test@example.com", subject);
        assertNotNull(issuedAt);
    }

    @Test
    void generateToken_ShouldIncludeAuthorities() {
        // Act
        String token = jwtService.generateAccessToken(userDetails);

        // Assert
        @SuppressWarnings("unchecked")
        java.util.List<String> authorities = jwtService.extractClaim(token, claims -> claims.get("authorities", java.util.List.class));

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.getFirst());
    }
}