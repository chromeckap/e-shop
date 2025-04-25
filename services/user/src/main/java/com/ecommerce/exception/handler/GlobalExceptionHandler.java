package com.ecommerce.exception.handler;

import com.ecommerce.exception.PasswordsNotEqualException;
import com.ecommerce.exception.UnauthorizedAccessException;
import com.ecommerce.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        log.error("User already exists: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(PasswordsNotEqualException.class)
    public ProblemDetail handlePasswordsNotEqualException(PasswordsNotEqualException exception) {
        log.error("Passwords do not match: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException() {
        log.error("Invalid login credentials");
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Špatně zadané přihlašovací údaje.");
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ProblemDetail handleUnauthorizedAccessException(UnauthorizedAccessException exception) {
        log.error("Null pointer error: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ProblemDetail handleNullPointerException(NullPointerException exception) {
        log.error("Null pointer error: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        log.error("Access denied: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Nemáš dostatečné oprávnění.");
    }

}
