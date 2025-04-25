package com.ecommerce.user;

import com.ecommerce.exception.PasswordsNotEqualException;
import com.ecommerce.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void validateUserDoesNotExist(String email) {
        boolean userExists = userRepository.findByEmail(email).isPresent();

        log.debug("Checking if user with email {} exists.", email);
        if (userExists)
            throw new UserAlreadyExistsException("Uživatel s tímto e-mailem již existuje.");
    }

    public void validatePasswordsEqual(String password, String confirmPassword) {
        log.debug("Checking if passwords match.");
        if (!password.equals(confirmPassword))
            throw new PasswordsNotEqualException("Zadaná hesla se neshodují.");
    }

    public void validateCurrentPasswordEquals(String password, String storedPassword) {
        log.debug("Checking if current password matches the stored password.");
        if (!passwordEncoder.matches(password, storedPassword))
            throw new PasswordsNotEqualException("Aktuální heslo se neshoduje se zadaným heslem.");
    }
}
