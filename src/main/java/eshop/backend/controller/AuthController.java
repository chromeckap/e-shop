package eshop.backend.controller;


import eshop.backend.exception.IncorrectPasswordException;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.exception.UsernameAlreadyExistsException;
import eshop.backend.model.User;
import eshop.backend.request.ChangePasswordRequest;
import eshop.backend.request.RegisterRequest;
import eshop.backend.response.AuthenticationResponse;
import eshop.backend.request.LoginRequest;
import eshop.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) throws UsernameAlreadyExistsException {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> logIn(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.logIn(request));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) throws IncorrectPasswordException {
        userService.changePassword(request, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(userService.list());
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) throws UserNotFoundException {
        userService.delete(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        userService.refreshToken(request, response);
    }
}