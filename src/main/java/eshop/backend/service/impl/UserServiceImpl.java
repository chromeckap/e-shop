package eshop.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eshop.backend.config.jwt.JwtService;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.exception.UsernameAlreadyExistsException;
import eshop.backend.exception.IncorrectPasswordException;
import eshop.backend.model.Token;
import eshop.backend.model.User;
import eshop.backend.repository.TokenRepository;
import eshop.backend.repository.UserRepository;
import eshop.backend.request.ChangePasswordRequest;
import eshop.backend.request.LoginRequest;
import eshop.backend.request.RegisterRequest;
import eshop.backend.response.AuthenticationResponse;
import eshop.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static eshop.backend.utils.EntityUtils.findByIdOrElseThrow;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse register(RegisterRequest request) throws UsernameAlreadyExistsException {
        if (userRepository.existsByEmail(request.email())){
            throw new UsernameAlreadyExistsException(request.email() + " already exists");
        } else if (!request.password().equals(request.confirmationPassword())) {
            throw new RuntimeException("Passwords not equal."); //todo own exception
            
        }
        var user = new User(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    @Override
    public AuthenticationResponse logIn(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var user = userRepository.findByEmail(request.email()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    @Override
    public void changePassword(ChangePasswordRequest request, Principal principal) throws IncorrectPasswordException {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword()))
            throw new IncorrectPasswordException();

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);
    }

    @Override
    public List<User> list() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long userId) throws UserNotFoundException {
        User user = findByIdOrElseThrow(userId, userRepository, UserNotFoundException::new);

        userRepository.delete(user);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = new Token(user, jwtToken);
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllByUser(user);

        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = new AuthenticationResponse(accessToken, refreshToken);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}