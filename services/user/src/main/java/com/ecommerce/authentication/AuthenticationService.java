package com.ecommerce.authentication;

import com.ecommerce.authorization.Role;
import com.ecommerce.config.JwtService;
import com.ecommerce.exception.InvalidTokenException;
import com.ecommerce.exception.UnauthorizedAccessException;
import com.ecommerce.exception.UserNotFoundException;
import com.ecommerce.token.TokenMapper;
import com.ecommerce.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenMapper tokenMapper;

    /**
     * Registers a new user after validating that the user does not exist and that the provided
     * passwords match.
     *
     * @param request the registration request containing the user details.
     */
    @Transactional
    public void register(RegisterRequest request) {
        Objects.requireNonNull(request, "Požadavek na registraci nesmí být prázdný");
        log.debug("Registering user with email: {}", request.email());

        userValidator.validateUserDoesNotExist(request.email());
        userValidator.validatePasswordsEqual(request.password(), request.confirmPassword());

        User user = userMapper.toUser(request);

        if (userRepository.findAll().isEmpty())
            user.setRole(Role.ADMIN);

        userRepository.save(user);
        log.info("User successfully registered with email: {}", request.email());
    }

    /**
     * Authenticates a user using the provided login request.
     * On successful authentication, generates and sets the access and refresh tokens as cookies in the response.
     *
     * @param request  the login request containing credentials.
     * @param response the HttpServletResponse to which token cookies will be added.
     */
    @Transactional
    public UserResponse login(LoginRequest request, HttpServletResponse response) {
        log.debug("Attempting authentication for user with email: {}", request.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = (User) authentication.getPrincipal();

        this.generateAndSetTokens(response, user);
        log.info("User successfully authenticated with email: {}", request.email());

        return userMapper.toResponse(user);
    }

    private void generateAndSetTokens(HttpServletResponse response, User user) {
        String accessToken = jwtService.generateAccessToken(user);
        ResponseCookie accessTokenCookie = tokenMapper.toAccessCookie(accessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        log.debug("Access token generated and set as cookie for user with id: {}", user.getId());

        String refreshToken = jwtService.generateRefreshToken(user);
        ResponseCookie refreshTokenCookie = tokenMapper.toRefreshCookie(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        log.debug("Refresh token generated and set as cookie for user with id: {}", user.getId());
    }


    /**
     * Refreshes the user's tokens by extracting the refresh token from cookies,
     * validating it, and generating new tokens.
     *
     * @param request  the HttpServletRequest from which the refresh token is extracted.
     * @param response the HttpServletResponse to which new token cookies will be added.
     */
    @Transactional
    public UserResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Attempting token refresh.");
        try {
            String refreshToken = Optional.ofNullable(jwtService.extractTokenFromCookies(request, "refresh_token"))
                    .orElseThrow(() -> new InvalidTokenException("Obnovovací token nesmí chybět."));

            String email = Optional.ofNullable(jwtService.extractEmail(refreshToken))
                    .orElseThrow(() -> new InvalidTokenException("Špatný formát obnovovacího tokenu."));

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Uživatel nebyl nalezen."));

            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new InvalidTokenException("Neplatný obnovovací token.");
            }

            this.generateAndSetTokens(response, user);
            log.info("Token successfully refreshed for user with email: {}", email);

            return userMapper.toResponse(user);

        } catch (InvalidTokenException exception) {
            tokenMapper.discardRefreshCookie();
            tokenMapper.discardAccessCookie();
            log.warn("Token refresh failed: {}", exception.getMessage());
            throw exception;
        }
    }

    public UserResponse refreshUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            throw new UnauthorizedAccessException("Nemáš dostatečné oprávnění.");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "Uživatel nebyl nalezen."
                ));

        return userMapper.toResponse(user);
    }

    /**
     * Generates access and refresh tokens for the specified user and sets them as HttpOnly cookies in the response.
     *
     * @param response the HttpServletResponse to which tokens will be added.
     * @param user     the authenticated user.
     */

    /**
     * Validates the current request by extracting authentication details from the SecurityContext.
     *
     * @return a GatewayUserResponse containing the user's id, email, and roles.
     */
    @Transactional
    public GatewayUserResponse validateRequest() {
        log.debug("Trying to validate authenticated user.");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("No authenticated user found.");
        }

        Object principal = authentication.getPrincipal();

        Long id = null;
        if (principal instanceof User)
            id = ((User) principal).getId();

        String username = authentication.getName();
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        log.info("Request validated for user: {}", username);
        return GatewayUserResponse.builder()
                .id(id)
                .email(username)
                .roles(authorities)
                .build();
    }
}
