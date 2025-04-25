package com.ecommerce.authentication;

import com.ecommerce.user.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.email());
        authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        log.info("Attempting to login user with email: {}", request.email());
        UserResponse userResponse = authenticationService.login(request, response);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("Attempting to refresh token.");
        UserResponse userResponse = authenticationService.refreshToken(request, response);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    public ResponseEntity<UserResponse> refreshUserInfo() {
        log.info("Refreshing user's info.");
        UserResponse userResponse = authenticationService.refreshUserInfo();
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/validate")
    public ResponseEntity<GatewayUserResponse> validateRequest(HttpServletRequest request) {
        log.info("Received request to validate token");
        GatewayUserResponse response = authenticationService.validateRequest();
        return ResponseEntity.ok(response);
    }
}
