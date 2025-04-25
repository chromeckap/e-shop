package com.ecommerce.exception.handler;

import com.ecommerce.exception.CategoryNotFoundException;
import com.ecommerce.exception.InfiniteLoopException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(CategoryNotFoundException.class)
    public ProblemDetail handleCategoryNotFoundException(CategoryNotFoundException exception) {
        log.error("Category not found: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InfiniteLoopException.class)
    public ProblemDetail handleInfiniteLoopException(InfiniteLoopException exception) {
        log.error("Infinite loop detected: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ProblemDetail handleNullPointerException(NullPointerException exception) {
        log.error("Null pointer error: {}", exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

}
