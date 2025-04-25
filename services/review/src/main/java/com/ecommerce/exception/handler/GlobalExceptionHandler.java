package com.ecommerce.exception.handler;

import com.ecommerce.exception.ReviewExistsException;
import com.ecommerce.exception.ReviewNotFoundException;
import com.ecommerce.exception.UnauthorizedAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ReviewExistsException.class)
    public ProblemDetail handleReviewExistsException(ReviewExistsException exception) {
        log.error("Review already exists: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ProblemDetail handleReviewNotFoundException(ReviewNotFoundException exception) {
        log.error("Review not found: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ProblemDetail handleUnauthorizedAccessException(UnauthorizedAccessException exception) {
        log.error("Unauthorized access: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ProblemDetail handleNullPointerException(NullPointerException exception) {
        log.error("Null pointer error: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

}
