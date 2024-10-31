package eshop.backend.service;

import eshop.backend.exception.IncorrectPasswordException;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.exception.UsernameAlreadyExistsException;
import eshop.backend.model.User;
import eshop.backend.request.ChangePasswordRequest;
import eshop.backend.request.LoginRequest;
import eshop.backend.request.RegisterRequest;
import eshop.backend.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface UserService {
    AuthenticationResponse register(RegisterRequest user) throws UsernameAlreadyExistsException;
    List<User> list();
    void delete(Long userId) throws UserNotFoundException;
    void changePassword(ChangePasswordRequest request, Principal principal)  throws IncorrectPasswordException;

    AuthenticationResponse logIn(LoginRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
